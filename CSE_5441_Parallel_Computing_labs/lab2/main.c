/*
 * cse 5441 lab 2
 * Author: Mengfan Zhu(zhu.2420)
 */
#include "main.h"

/*
 * read a line from stdin
 */
void read_line(char* line){
  int i = 0;
  while((line[i] = getchar())!='\n'){
    i++;
  }
  i++;
  line[i] = '\0';
  return;
}
/*
 * check whether the character is a valid cmd
 * 0 means invalid, 1 and 2 means valid
 * specifically, 2 represents end cmd X
 */
int valid_cmd(char cmd){
    int result = 0;
    if(cmd == 'A' || cmd == 'B' || cmd == 'C' || cmd == 'D'){
        result = 1;
    }
    else{
        if(cmd == 'X'){
            result = 2;
        }
    }
    return result;
}
/*
 * get the number from the line
 */
uint16_t get_number(char *line){
    int i = 1, result = 0, flag = 1;
    char ch;
    /* find first number */
    while(i < strlen(line) && flag){
        ch = line[i];
        if(ch >= '0' && ch <= '9'){
            flag = 0;
        }
        else{
            i++;
        }
    }
    flag = 1;
    /* get whole value */
     while(i < strlen(line) && flag){
        ch = line[i];
        if(ch >= '0' && ch <= '9'){
            result = result * 10 + line[i] - '0';
            i++;
        }
        else{
            flag = 0;
        }
    }
    return result;
}
/*
 * call corresponding transform function based on cmd
 * return the transformed key
 */
uint16_t trans_call(char cmd, uint16_t key){
    uint16_t result;
    switch(cmd){
        case 'A':
            result = transformA(key);
            break;
        case 'B':
            result = transformB(key);
            break;
        case 'C':
            result = transformC(key);
            break;
        case 'D':
            result = transformD(key);
            break;
    }
    return result;
}
/*
 * producer
 * return 0 when get X, return 1 when filling the work queue
 */
void* producer(void *parameters){
    char line[LINE_SIZE], cmd;
    int valid_flag, i, key, add_flag;
    struct thread_para *para;
    struct work_entry* work_queue;
    time_t t_begin, t_end;
    time(&t_begin);
    /* get passing parameters */
    para = (struct thread_para*)parameters;
    work_queue = para->work_queue;
    while(!para->prod_end){
        add_flag = 0;
        /* lock for producer*/ 
        pthread_mutex_lock(&para->prod_mutex); 
        if(!para->prod_end){
            /* read line */
            read_line(line);
            cmd = line[0];
            valid_flag = valid_cmd(cmd);
            /* add to queue */
            i = (para->prod_index) % QUEUE_SIZE;
            if(valid_flag == 2){
                para->prod_end = 1;
                /* lock for fill queue */
                add_flag = 1;
            }
            if(valid_flag == 1){
                key = get_number(line);
                /* check valid key */
                if(key >= 0 && key <= 1000){
                    /* lock for fill queue */
                    add_flag = 1;                  
                    para->prod_index++;
                }
            } 
        }
        pthread_mutex_unlock(&para->prod_mutex); 
        if(add_flag){
            sem_wait(&para->empty);
            if(valid_flag == 1){
                work_queue[i].cmd = cmd;
                work_queue[i].key = trans_call(cmd, key);
            }
            else{
                work_queue[i].cmd = cmd;
            }
            sem_post(&para->full);
        }
    }
    time(&t_end);
    para->prod_time += (t_end - t_begin);
    /* end thread */
    if(para->prod_end){
        pthread_exit(0);
    }
}
/*
 * consumer
 * return 0 when get X, return 1 when fill the work queue
 */
void* consumer(void *parameters){
    int i, count,valid_flag;
    char cmd;
    uint16_t encoded_key, decoded_key;
    struct thread_para *para;
    struct work_entry* work_queue;
    struct work_entry entry;
    time_t t_begin, t_end;
    time(&t_begin);
    /* get passing parameters */
    para = (struct thread_para*)parameters;
    work_queue = para->work_queue;
    /* extract and print (on standard output) */
    while(!para->cons_end){
        valid_flag = 0;
         /* lock for consumer*/
        pthread_mutex_lock(&para->cons_mutex);
        if(!para->cons_end){
            /* lock for export data from queue */
            sem_wait(&para->full); 
            i = (para->cons_index) % QUEUE_SIZE;
            entry = work_queue[i];
            sem_post(&para->empty);
            /* process data and print out*/
            count = para->cons_index + 1;
            para->cons_index++;
            cmd = entry.cmd;
            valid_flag = valid_cmd(cmd);
            if(valid_flag == 2){
                para->cons_end = 1;
            }
            else{
                encoded_key = entry.key;
                decoded_key = trans_call(cmd, encoded_key);
            }            
        }
        pthread_mutex_unlock(&para->cons_mutex); 
        if(valid_flag == 1){
            while(1){
                if(para->print_index == count-1){ 
                    pthread_mutex_lock(&para->cons_mutex2);                
                    printf("%-5d Q:%-5d %-5c %-5d %-5d\n",count, i, cmd, encoded_key, decoded_key);
                    para->print_index++;
                    pthread_mutex_unlock(&para->cons_mutex2);
                    break; 
                }
            }
        } 
    }
    time(&t_end);
    para->cons_time += (t_end - t_begin);
    /* end thread */
    if(para->cons_end){
        pthread_exit(0);
    }
}
/*
 * print time
 */
void print_time(time_t  p_time, time_t c_time){
    if(p_time < 60 || c_time < 60){
        /* print in seconds if any of the time is less than 1 minute. */
        printf("Total run-time of producer: %3d seconds.\n",p_time);
        printf("Total run-time of consumer: %3d seconds.\n",c_time);
    }
    else{
        /* print in minutes if all time is larger than 1 minute */
        printf("Total run-time of producer: %5.2f minutes.\n",(double)p_time/60);
        printf("Total run-time of consumer: %5.2f minutes.\n",(double)c_time/60);
    }
    return;
}


/*
 * main function
 */
int main(int argc, char *argv[]) {
    /* variables */
    struct work_entry work_queue[QUEUE_SIZE];
    pthread_t* prod_threads, *cons_threads;
    struct thread_para para;
    void *thread_status;
    int prod_num, cons_num, i;
    int count = 1; //count valid pairs
    /* set the number of threads for producer and consumer */
    if(argc == 3){
      prod_num = atoi(argv[1]);
      cons_num = atoi(argv[2]);
    }
    else{
       /* default */
      prod_num = 2;
      cons_num = 2;
    }
    /* allocate memory for threads */
    prod_threads = (pthread_t*) malloc(prod_num * sizeof(pthread_t));
    cons_threads = (pthread_t*) malloc(cons_num * sizeof(pthread_t));
    /* init thread-global parameters to be passed */
    para.work_queue = work_queue;
    para.prod_end = 0;
    para.cons_end = 0;
    para.prod_index = 0;
    para.cons_index = 0;
    para.print_index = 0;
    para.prod_time = 0;
    para.cons_time = 0;
    pthread_mutex_init(&para.prod_mutex, NULL);
	pthread_mutex_init(&para.cons_mutex, NULL);
    pthread_mutex_init(&para.cons_mutex2, NULL);
    sem_init(&para.full, 0, 0);
	sem_init(&para.empty, 0, QUEUE_SIZE);
    /* create threads */
    for(i = 0; i < prod_num; i++){
        pthread_create(&prod_threads[i], NULL, producer, (void *) &para);
    }
    for(i = 0; i < cons_num; i++){
        pthread_create(&cons_threads[i], NULL, consumer, (void *) &para);
    }
    /* join threads */
    for(i = 0; i < prod_num; i++){
         pthread_join(prod_threads[i],&thread_status);
    }
    for(i = 0; i < cons_num; i++){
         pthread_join(cons_threads[i],&thread_status);
    }
    /* free memory */
    free(prod_threads);
    free(cons_threads);
    print_time(para.prod_time, para.cons_time);
    return 0;
}