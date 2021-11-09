#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <omp.h>
#define QUEUE_SIZE 50
#define LINE_SIZE 100
struct work_entry {
    char cmd;
    uint16_t key;
};
uint16_t transformA(uint16_t input_val);
uint16_t transformB(uint16_t input_val);
uint16_t transformC(uint16_t input_val);
uint16_t transformD(uint16_t input_val);
int valid_cmd(char cmd);
uint16_t get_number(char *line);
uint16_t trans_call(char cmd, uint16_t key);
int producer(struct work_entry *work_queue, int* max_size);
int consumer(struct work_entry *work_queue, int max_size, int count);
