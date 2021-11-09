#include "main.h"


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
 * return 0 when get X, return 1 when fill the work queue
 */
int producer(struct work_entry *work_queue, int* max_size){
    char *line = NULL;
    struct work_entry temp[QUEUE_SIZE];
    char cmd;
    uint16_t key;
    size_t len = 0;
    int valid_flag, end = 0, i = 0,j;
    /* read from input and fill in temp queue */
    while(i < QUEUE_SIZE && !end){
        /* read input command */
        getline(&line, &len, stdin);
        /* handle the line */
        cmd = line[0];
        valid_flag = valid_cmd(cmd);
        /* end when cmd is X */
        if(valid_flag == 2){
            end = 1;
            temp[i].cmd = cmd;
            *max_size = i + 1;
        }else{
            /* fill the work_queue */
            if(valid_flag == 1){
                key = get_number(line);
                /* check valid key */
                if(key >= 0 && key <= 1000){
                    temp[i].cmd = cmd;
                    temp[i].key = key;
                    i++;
                }                             
            }
        }
    } 
    #pragma omp parallel for
    for(j = 0; j < *max_size; j ++){
        if(omp_get_thread_num() == 0){
            fprintf(stderr, "producer: %d threads\n", omp_get_num_threads());
        }
        fprintf(stderr, "j: %d, thread: %d\n", j, omp_get_thread_num());
        work_queue[j].cmd = temp[j].cmd; 
        if(work_queue[j].cmd != 'X'){
            work_queue[j].key = trans_call(temp[j].cmd, temp[j].key);
        }
    }  

    return !end;
}
/* 
 * consumer
 * return 0 when get X, return 1 when fill the work queue
 */
int consumer(struct work_entry *work_queue, int max_size, int count){
    int i, end = 0;
    char temp[QUEUE_SIZE][LINE_SIZE];
    #pragma omp parallel for
    for(i = 0; i < max_size; i++){
        char cmd;
        uint16_t encoded_key, decoded_key;
        if(omp_get_thread_num() == 0){
            fprintf(stderr, "consumer: %d threads\n", omp_get_num_threads());
        }
        fprintf(stderr, "i: %d, thread: %d\n", i, omp_get_thread_num());
        cmd = work_queue[i].cmd; 
        if(cmd != 'X'){
            encoded_key = work_queue[i].key;
            decoded_key = trans_call(cmd, encoded_key);
            sprintf(temp[i], "%-5d Q:%-5d %-5c %-5d %-5d\n",count*QUEUE_SIZE+i+1, i, cmd, encoded_key, decoded_key); 
        }
        else{
            end = 1;
        }
    }  
    if(end == 1){
        max_size--;
    }
    /* output */
    for(i = 0; i < max_size; i++){
        printf(temp[i]);
    }
    return !end;
}

/*
 * main function
 */
int main(int argc, char *argv[]) {
    /* declare work queue */
    int count = 0;
    struct work_entry work_queue[QUEUE_SIZE];
    int flag_p = 1, flag_c = 1, max_size = QUEUE_SIZE;
    while(flag_p || flag_c){
        if(flag_p){
            flag_p = producer(work_queue, &max_size);
        }
        if(flag_c){
            flag_c = consumer(work_queue, max_size, count);
            count++;
        }   
    }
    return 0;
}