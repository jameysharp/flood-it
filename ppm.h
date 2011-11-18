#ifndef PPM_H
#define PPM_H

#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

#define die(fmt...) do { fprintf(stderr, fmt); exit(EXIT_FAILURE); } while(0)

struct pixel {
	uint8_t red;
	uint8_t green;
	uint8_t blue;
};

struct pixel *read_ppm(unsigned int *width, unsigned int *height, unsigned int *maxval);

#endif /* PPM_H */
