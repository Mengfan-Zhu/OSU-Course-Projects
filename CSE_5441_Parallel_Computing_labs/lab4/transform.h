#include <stdio.h>
#include <stdlib.h>
#include <malloc.h>
#include <cuda.h>
#include <cuda_runtime.h>

#define NUM_THREAD 256
struct work_entry {
    char cmd;
    uint16_t key;
};
__device__ uint16_t transformA(uint16_t input_val);
__device__ uint16_t transformB(uint16_t input_val);
__device__ uint16_t transformC(uint16_t input_val);
__device__ uint16_t transformD(uint16_t input_val);
