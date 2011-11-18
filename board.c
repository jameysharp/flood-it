#include "ppm.h"

struct {
	struct pixel ref;
	char color;
} colors[] = {
	{ { 0xED, 0x70, 0xA1 }, 'p' },
	{ { 0xDC, 0x4A, 0x20 }, 'r' },
	{ { 0x60, 0x5C, 0xA8 }, 'b' },
	{ { 0x46, 0xB1, 0xE2 }, 'c' },
	{ { 0xF3, 0xF6, 0x1D }, 'y' },
	{ { 0x7E, 0x9D, 0x1E }, 'g' },
};

static char color(struct pixel *pixel)
{
	unsigned int i;
	for(i = 0; i < sizeof(colors) / sizeof(*colors); ++i)
		if(colors[i].ref.red == pixel->red &&
		   colors[i].ref.green == pixel->green &&
		   colors[i].ref.blue == pixel->blue)
			return colors[i].color;
	die("\nunknown color #%02x%02x%02x\n", pixel->red, pixel->green, pixel->blue);
}

int main(void)
{
	unsigned int width, height, maxval;
	struct pixel *buf = read_ppm(&width, &height, &maxval);

	while(height > 0)
	{
		unsigned int x;
		for(x = 0; x < width; ++x)
			putchar(color(buf++));
		printf("\n");
		--height;
	}
	exit(EXIT_SUCCESS);
}
