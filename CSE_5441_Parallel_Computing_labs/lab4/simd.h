#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <malloc.h>

#define ALLOC_SIZE 500
#define LINE_SIZE 100
#define QUEUE_SIZE 50

struct work_entry {
    char cmd;
    uint16_t key;
};
int valid_cmd(char cmd);
uint16_t get_number(char *line);
uint16_t trans_call(char cmd, uint16_t key);
int trans_index(char cmd);
int handle_input(struct work_entry *ori_queue, int* counts);
void handle_output(struct work_entry *ori_queue, struct work_entry *result_queue, int count);
void doTransform(struct work_entry *ori_queue, struct work_entry *result_queue, int count);
void get_max_min(struct work_entry *work_queue, int count);
int* reorder(struct work_entry *ori_queue, struct work_entry *reorder_queue, int count, int* counts);
void recover(struct work_entry *result_queue, struct work_entry *recover_queue, int *indexs, int count);