#include <stdio.h> 
#include <pthread.h> 
#define NUM 10
struct thread_para{
    int num;
}
void* say_hello(void* tid);

void* say_hello(void* p){
    struct thread_para para = (struct thread_para)p;
    printf("hello world from thread %d\n", para.num);
    pthread_exit(0);
}
int main(){
    pthread_t my_threads[NUM];
    void *thread_status;
    struct thread_para para;
    
    int tnum;
    for(tnum = 0; tnum < NUM; tnum++){
        para.num=tnum;
        pthread_create(&my_threads[tnum], NULL, say_hello, (void *) para);
    }
    for(tnum = 0; tnum < NUM; tnum++){
        pthread_join(my_threads[tnum],&thread_status);
    }
    return 0;
}