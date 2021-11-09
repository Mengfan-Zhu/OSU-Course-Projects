#include "arbitrary.h"


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
 * handle input file and add valid pairs into work_queue
 */
int handle_input(struct work_entry *ori_queue){
    char *line = NULL, cmd;
    uint16_t key;
    size_t len = 0;
    int count = 0, i = 0, end = 0, valid_flag;
    while(!end){     
        i = 0;
        while(i < ALLOC_SIZE){
            // read input command
            getline(&line, &len, stdin);
            // handle the line
            cmd = line[0];
            valid_flag = valid_cmd(cmd);
            // end when cmd is X 
            if(valid_flag == 2){
                end = 1;
                break;
            }
            else{
                // fill the work_queue
                if(valid_flag == 1){
                    key = get_number(line);
                    // check valid key
                    if(key >= 0 && key <= 1000){
                        ori_queue[count].cmd = cmd;
                        ori_queue[count].key = key;
                        count++;
                        i++;
                    }                             
                }
            }
        }
        // re-allocate memory
        if(!end){
            ori_queue = (struct work_entry *) realloc(ori_queue, (count + ALLOC_SIZE) * sizeof(struct work_entry));
        }
    }  
    ori_queue = (struct work_entry *) realloc(ori_queue, count * sizeof(struct work_entry));
    // return size
    return count;
}
/* 
 * output result
 */
void handle_output(struct work_entry *ori_queue, struct work_entry *result_queue, int count){
    int i, end = 0;
    for(i = 0; i < count; i++){
        char cmd;
        uint16_t encoded_key, decoded_key;
        cmd = ori_queue[i].cmd; 
        encoded_key = ori_queue[i].key;
        decoded_key = result_queue[i].key;
        printf("%-5d Q:%-5d %-5c %-5d %-5d\n",i + 1, i % QUEUE_SIZE, cmd, encoded_key, decoded_key);
    }  
}
/*
 * get max and min
 */
 void get_max_min(struct work_entry *work_queue, int count){
     int max_key = 0, min_key = 10000, i, temp;
     for(i = 0; i < count; i++){
         temp = work_queue[i].key;
         if(temp < min_key){
             min_key = temp;
         }
         if(temp > max_key){
             max_key = temp;
         }
     }
     fprintf(stderr, "max: %d, min: %d\n", max_key, min_key);
 }
/*
 * main function
 */
int main(int argc, char *argv[]) {
    int count;
    struct work_entry* ori_queue, *result_queue;
    ori_queue = (struct work_entry *) malloc(ALLOC_SIZE * sizeof(struct work_entry));
    count = handle_input(ori_queue);
    result_queue = (struct work_entry *) malloc(count * sizeof(struct work_entry));
    doTransform(ori_queue, result_queue, count);
    get_max_min(ori_queue, count);
    handle_output(ori_queue, result_queue, count);
    free(ori_queue);
    free(result_queue);
    return 0;
}