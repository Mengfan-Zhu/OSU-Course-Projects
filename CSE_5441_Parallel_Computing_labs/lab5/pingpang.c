#include "pingpang.h"
int main(int argc, char* argv[])
{
    int rank, size, i, j;
    int msg_size[5] = {32, 256, 512, 1024, 2048};
    clock_t begin, end;
    double msgA[MSG_MAX_SIZE]={0}, msgB[MSG_MAX_SIZE]={1}, bandwidth, avg_time;
    MPI_Status status;
    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    for (j = 0; j < 5; j++){       
        MPI_Barrier(MPI_COMM_WORLD);
        begin = clock();
        for(i = 0; i < IT_SIZE; i++){
            if (rank == 0) {            
                MPI_Send(msgA, msg_size[j], MPI_DOUBLE, 1, i, MPI_COMM_WORLD);
                MPI_Recv(msgB, msg_size[j], MPI_DOUBLE, 1, i, MPI_COMM_WORLD, &status);
                MPI_Send(msgB, msg_size[j], MPI_DOUBLE, 1, i, MPI_COMM_WORLD);
                MPI_Recv(msgA, msg_size[j], MPI_DOUBLE, 1, i, MPI_COMM_WORLD, &status);      
            }
            else if(rank == 1) {
                MPI_Recv(msgA, msg_size[j], MPI_DOUBLE, 0, i, MPI_COMM_WORLD, &status);
                MPI_Send(msgB, msg_size[j], MPI_DOUBLE, 0, i, MPI_COMM_WORLD);
                MPI_Recv(msgB, msg_size[j], MPI_DOUBLE, 0, i, MPI_COMM_WORLD, &status);
                MPI_Send(msgA, msg_size[j], MPI_DOUBLE, 0, i, MPI_COMM_WORLD);    
            }
        }
        MPI_Barrier(MPI_COMM_WORLD);
        end = clock(); 
        avg_time = (double)(end - begin) / (4 * IT_SIZE);
        bandwidth = (double)sizeof(double) * msg_size[j] / avg_time;        
        fprintf(stderr, "Rank %d: bandwidth with size %d: %f\n", rank, msg_size[j], bandwidth);
    }
    MPI_Finalize();
    return(0);
} 