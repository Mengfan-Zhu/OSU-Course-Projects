#include "transform.h"
#define gpuErrchk(ans) { gpuAssert((ans), __FILE__, __LINE__); }
inline void gpuAssert(cudaError_t code, const char *file, int line, bool abort=true)
{
   if (code != cudaSuccess) 
   {
      fprintf(stderr,"GPUassert: %s %s %d\n", cudaGetErrorString(code), file, line);
      if (abort) exit(code);
   }
}
// extern "C" void doTransform(struct work_entry *ori_queue, struct work_entry *result_queue, int count);
// call corresponding transform functions
__device__ uint16_t transCall(char cmd, uint16_t key){
    uint16_t result;
    if(cmd == 'A'){
        result = transformA(key); 
    }
    else if(cmd == 'B'){
        result = transformB(key); 
    }
    else if(cmd == 'C'){
        result = transformC(key); 
    }
    else if(cmd == 'D'){
        result = transformD(key); 
    }
    return result;
}
// kernel - producer
__global__ void transProducer(struct work_entry *work_queue, int count){
    int tid;
    tid = blockIdx.x * blockDim.x + threadIdx.x;
    if(tid < count){
        work_queue[tid].key = transCall(work_queue[tid].cmd, work_queue[tid].key);   
    }
}
// kernel - consumer
__global__ void transConsumer(struct work_entry *work_queue, int count){
    int tid;
    tid = blockIdx.x * blockDim.x + threadIdx.x;
    if(tid < count){
        work_queue[tid].key = transCall(work_queue[tid].cmd, work_queue[tid].key);   
    }
}

extern "C" void doTransform(struct work_entry *ori_queue, struct work_entry *result_queue, int count){
    // pointers for host and device memory
    struct work_entry *d_queue;
    // thread hierarchy
    int num_threads = NUM_THREAD;
    int num_blocks = count/num_threads + 1;
    time_t p_time, c_time, p_begin, p_end, c_begin, c_end;
    // allocate memory
    size_t memSize;
    memSize = count * sizeof(struct work_entry);
    cudaMalloc((void**)&d_queue, memSize);
    time(&p_begin);
    // copy data
    cudaMemcpy(d_queue, ori_queue, memSize, cudaMemcpyHostToDevice);
    // launch kernel
    dim3 dimGrid(num_blocks);
    dim3 dimBlock(num_threads);
    // do transformation and copy results
    transProducer<<<dimGrid, dimBlock>>>(d_queue, count);
    cudaMemcpy(ori_queue, d_queue, memSize, cudaMemcpyDeviceToHost);
    time(&p_end);
    p_time = p_end - p_begin;
    time(&c_begin);
    transConsumer<<<dimGrid, dimBlock>>>(d_queue, count);
    cudaMemcpy(result_queue, d_queue, memSize, cudaMemcpyDeviceToHost);
    time(&c_end);
    c_time = c_end - c_begin;
    fprintf(stderr, "total producer time: %d\ntotal consumer time: %d\n", p_time, c_time);
    cudaFree(d_queue);
}