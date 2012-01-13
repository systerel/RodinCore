/*
 * Small program simulating an external prover that can be friendly and respond
 * to TERM signal, or, when given some argument on the command line be
 * unfriendly and ignore this signal.
 */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>

static void action(int signal)
{
   fprintf(stderr, "Friend received signal %d.\n", signal);
   exit(signal);
}

int main(int argc, const char *argv[])
{
  /* Be cooperative only when run without argument. */
  if (argc <= 1)
    signal(SIGTERM, action);
  else
    signal(SIGTERM, SIG_IGN);

  sleep(100);
  return 0;
}
