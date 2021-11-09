#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <pthread.h>
#include <semaphore.h>
#define QUEUE_SIZE 50
#define LINE_SIZE 50
struct work_entry {
    char cmd;
    uint16_t key;
}; 
struct thread_para{
    struct work_entry* work_queue; 
    int prod_end, cons_end; 
    pthread_mutex_t prod_mutex, cons_mutex,cons_mutex2;	
    int prod_index, cons_index, print_index;
    sem_t empty, full;
    time_t prod_time, cons_time;
};
uint16_t transformA(uint16_t input_val);
uint16_t transformB(uint16_t input_val);
uint16_t transformC(uint16_t input_val);
uint16_t transformD(uint16_t input_val);
int valid_cmd(char cmd);
uint16_t get_number(char *line);
uint16_t trans_call(char cmd, uint16_t key);
void* producer(void *parameters);
void* consumer(void *parameters);
void print_time(time_t  p_time, time_t c_time);