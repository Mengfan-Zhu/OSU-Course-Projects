all: lab4_mengfan_zhu.2420_arbitrary lab4_mengfan_zhu.2420_simd
lab4_mengfan_zhu.2420_arbitrary: arbitrary.c transform.o transform_nvcc.o
        nvcc arbitrary.c transform.o transform_nvcc.o -lcudart -lcuda -o lab4_mengfan_zhu.2420_arbitrary
lab4_mengfan_zhu.2420_simd: simd.c transform.o transform_nvcc.o
        nvcc simd.c transform.o transform_nvcc.o -lcudart -lcuda -o lab4_mengfan_zhu.2420_simd
transform.o: transform.cu
        nvcc -dc transform.cu
