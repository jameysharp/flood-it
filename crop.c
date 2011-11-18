#include "ppm.h"

static int is_unsaturated(struct pixel *buf, unsigned int count, unsigned int stride)
{
	unsigned int i;
	for(i = 0; i < count; ++i)
	{
		struct pixel *p = buf + i * stride;
		if(p->red != p->green || p->green != p->blue)
			return 0;
	}
	return 1;
}

int main(void)
{
	unsigned int width, height, maxval, stride;
	struct pixel *buf = read_ppm(&width, &height, &maxval);

	while(height > 0 && is_unsaturated(buf, width, 1))
	{
		buf += width;
		--height;
	}

	while(height > 0 && is_unsaturated(buf + (height - 1) * width, width, 1))
		--height;

	stride = width;

	while(width > 0 && is_unsaturated(buf, height, stride))
	{
		++buf;
		--width;
	}

	while(width > 0 && is_unsaturated(buf + (width - 1), height, stride))
		--width;

	printf("P6\n%u %u\n%u\n", width, height, maxval);
	while(height > 0)
	{
		fwrite(buf, sizeof(*buf), width, stdout);
		buf += stride;
		--height;
	}

	exit(EXIT_SUCCESS);
}
