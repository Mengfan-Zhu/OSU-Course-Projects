#include "main.h"
int count = 1;

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
int producer(struct work_entry *work_queue){
    char *line = NULL;
    size_t len = 0;
    int valid_flag, end, i, key;
    char cmd;
    end = 0;
    i = 0;
    while(i < QUEUE_SIZE && !end){
        /* read input command */
        getline(&line, &len, stdin);
        /* handle the line */
        cmd = line[0];
        valid_flag = valid_cmd(cmd);
        /* end when cmd is X */
        if(valid_flag == 2){
            end = 1;
            work_queue[i].cmd = cmd;
        }else{
            /* fill the work_queue */
            if(valid_flag == 1){
                key = get_number(line);
                /* check valid key */
                if(key >= 0 && key <= 1000){
                    work_queue[i].cmd = cmd;               
                    work_queue[i].key = trans_call(cmd, key);
                    i++;
                }                             
            }
        }
    }   
    return !end;
}
/* 
 * consumer
 * return 0 when get X, return 1 when fill the work queue
 */
int consumer(struct work_entry *work_queue){
    int i, end;
    char cmd;
    end = 0;
    i = 0;
    uint16_t encoded_key, decoded_key;
    /* print (on standard output) */
    while(i < QUEUE_SIZE && !end){
        cmd = work_queue[i].cmd;
        if(cmd != 'X'){
            encoded_key = work_queue[i].key;
            decoded_key = trans_call(cmd, encoded_key);
            printf("%-5d Q:%-5d %-5c %-5d %-5d\n",count++, i, cmd, encoded_key, decoded_key);
            i++;
        }
        else{
            end = 1;
        }
    }
    return !end;
}
void print_time(double total_time){
    if(p_time < 60 || c_time < 60){
        printf("Total run-time of producer: %5d seconds.\n",p_time);
        printf("Total run-time of consumer: %5d seconds.\n",c_time);
        printf("Total run-time of whole program: %5d seconds.\n",total_time);
    }
    else{
        printf("Total run-time of producer: %5.2f minutes.\n",(double)p_time/60);
        printf("Total run-time of consumer: %5.2f minutes.\n",(double)c_time/60);
        printf("Total run-time of whole program: %5.2f minutes.\n",(double)total_time/60);
    }
    return;
}
/*
 * main function
 */
int main(int argc, char *argv[]) {
    /* declare work queue */

    struct work_entry work_queue[QUEUE_SIZE];
    int flag_p, flag_c;
    flag_p = 1;
    flag_c = 1;
    while(flag_p || flag_c){
        if(flag_p){
            flag_p = producer(work_queue);
        }
        if(flag_c){
            flag_c = consumer(work_queue);
        }   
    }
    return 0;
}