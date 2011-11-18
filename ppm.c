#include "ppm.h"

struct pixel *read_ppm(unsigned int *width, unsigned int *height, unsigned int *maxval)
{
	struct pixel *buf;

	if(scanf("P6 %u %u %u", width, height, maxval) != 3 || getchar() != '\n')
		die("invalid or unsupported PPM header\n");
	if(*maxval > 255)
		die("can't handle two-byte PPM files (maxval=%u); try `pnmdepth 255`\n", *maxval);

	buf = malloc(sizeof(*buf) * *width * *height);
	if(!buf)
		die("no memory for image buffer\n");
	fread(buf, sizeof(*buf), *width * *height, stdin);

	return buf;
}
