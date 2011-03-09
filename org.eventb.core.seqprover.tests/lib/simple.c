/*
 *  Simple program similar to Unix cat used for testing the
 *  external prover support of Rodin.
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#ifdef WIN32
#include <windows.h>
#define sleep(t)   Sleep(1000 * (t))
#else
#include <unistd.h>
#endif

#define SLEEP_TIME 100

int main(int argc, char** argv)
{
  char *opt, *file;
  FILE *input, *output;
  int c;

  if (argc == 1)
    exit(EXIT_SUCCESS);

  opt = argv[1];

  if (0 == strcmp(opt, "-s")) {
    sleep(60);
    exit(EXIT_SUCCESS);
  }

  if (argc != 3)
    exit(EXIT_FAILURE);

  file = argv[2];

  /* Read the file descriptor on which to output. */
  if (0 == strcmp(opt, "-o")) {
    output = stdout;
  } else if (0 == strcmp(opt, "-e")) {
    output = stderr;
  } else {
    fprintf(stderr, "Unknown output");
    exit(EXIT_FAILURE);
  }

  /* Parse the input file and open it. */
  if (strlen(file) == 0 || 0 == strcmp(file, "-")) {
    input = stdin;
  } else {
    input = fopen(file, "r");
  }

  while ((c = fgetc(input)) != EOF) {
    fputc(c, output);
  }

  fclose(input);
  fclose(output);
  exit(EXIT_SUCCESS);
}
