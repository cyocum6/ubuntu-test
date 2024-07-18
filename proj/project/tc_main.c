/**
 * The main entry point of the thermocouple simulation program.
 *
 * This program simulates a thermocouple as a deamon process.
 * It is designed to be started at system startup in the init
 * (or alternative) hierarchy under linux. This module contains
 * the entry point (main()) at the bottom of the file. All
 * other functions are module private functions (i.e. static),
 * and not part of the exposed API. The only exposed function
 * is main().
 */
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <syslog.h>
#include <stdbool.h>
#include <argp.h>
#include <curl/curl.h>
#include <string.h>

#include "tc_error.h"
#include "tc_state.h"


//from homework
#define NO_ARG      0
#define OK          0
#define INIT_ERR    1
#define REQ_ERR     2
#define ERR_WTF     9

static char args_doc[] = "-u http://localhost:8000 -o 'argument'";
static char doc[] = "Performs http request for get, post, delete or put.";


//

//other resources
// params, move to config file
static const char* STATE_URL = "http://3.139.156.162:8080/state";
static const char* TEMP_URL = "http://3.139.156.162:8080/temp";
static const char* REPORT_URL = "http://3.139.156.162:8080/report";
//



static const char*  DAEMON_NAME     = "tcsimd";
static const char*  TEMP_FILENAME   = "/tmp/temp";
static const char*  STATE_FILENAME  = "/tmp/status";
static const char*  WORKING_DIR     = "/";

static const long   SLEEP_DELAY     = 5;


//from homework
// arguments will be used for storing values from command line
struct Arguments {
    char *arg;  // for string argument
    char *url;    
    bool post;
    bool get;
    bool put;
    bool delete;
};

// OPTIONS
// Name, Key, Arg, Flags, Doc
static struct argp_option options[] = {
    {"url", 'u', "String", NO_ARG, "URL for HTTP Request, REQUIRED"},
    {"post", 'o', NO_ARG, NO_ARG, "POST HTTP Request, requires VERB"},
    {"get", 'g', NO_ARG, NO_ARG, "GET HTTP Request"},
    {"put", 'p', NO_ARG, NO_ARG, "GET HTTP Request, requires VERB"},
    {"delete", 'd', NO_ARG, NO_ARG, "GET HTTP Request, requires VERB"},
    {NO_ARG}
};


int handle_error(char* message, struct argp_state *state) {
    printf("%s",message);
    argp_usage(state);
    return REQ_ERR;
}

// sending the http request
static int send_http_request(char *url, char *message, char *type, bool verb) {
    printf("%s request at url: %s\n", type, url);
    CURL *curl = curl_easy_init();
    if (curl) {
        CURLcode res;
        curl_easy_setopt(curl, CURLOPT_URL, url);
        curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, type);
        if (verb) {
            printf("message: %s\n", message);
            curl_easy_setopt(curl, CURLOPT_POSTFIELDS, message);
        } else {
            curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, 1L);
        }
        res = curl_easy_perform(curl);

        if (res != CURLE_OK) {
            return REQ_ERR;
        }

        curl_easy_cleanup(curl);
    } else {
        return INIT_ERR;
    }
    return OK;
}

// parsing inputs from options
static error_t parse_opt(int key, char *arg, struct argp_state *state) {
    struct Arguments *arguments = state->input;

    switch (key) {
        case 'u':
            arguments->url = arg;
            break;
        case 'o':
            arguments->post = true;
            break; 
        case 'g':
            arguments->get = true;
            break;
        case 'p':
            arguments->put = true;
            break;
        case 'd':
            arguments->delete = true;
            break;
        // standards from argp below, from urls in main ()
        case ARGP_KEY_NO_ARGS:  
        // provides post, put or delete but no url for argument          
            if (arguments->post == true || arguments->put == true || arguments->delete == true) {
                return handle_error("VERB required.\n", state);
            }
        case ARGP_KEY_ARG:    
        // provides post, put or delete but too much in url(s) for argument        
            if (state->arg_num >= 1) {
                printf("Use quotes around arguments.\n");
                argp_usage(state);
                return REQ_ERR;
            }
            arguments->arg = arg;
            break;
        case ARGP_KEY_END:   
        // Bad URL given because it's NULL or wrong, missing operation for URL.         
            if (arguments->url == NULL) {
                printf("Please provide a valid url.\n");
                argp_usage(state);
                return REQ_ERR;
            } else if (arguments->get == false && arguments->post == false && arguments->put == false && arguments->delete == false) {
                return handle_error("Select request type.\n", state);
            }
            break;
        // But if it succeeds....
        case ARGP_KEY_SUCCESS: 
        // Perform CURL based on Verb requested, limit one Verb           
            if (arguments->get) {
                int err = send_http_request(arguments->url, NULL, "GET", false);
                break;
            } else if (arguments->post) {
                int err = send_http_request(arguments->url, arguments->arg, "POST", true);
                break;
            } else if (arguments->put) {
                int err = send_http_request(arguments->url, arguments->arg, "PUT", true);
                break;
            } else if (arguments->delete) {
                int err = send_http_request(arguments->url, arguments->arg, "DELETE", true);
                break;
            }
            break;
        default:
            return ARGP_ERR_UNKNOWN;
    }
    return 0;
}
////////////////////////////////////////////////////////////////////////



/**
 * If we exit the process, we want to sent information on 
 * the reason for the exit to syslog, and then close
 * the log. This is a way for us to centralize cleanup
 * when we leave the daemon process.
 *
 * @param err The error code we exit under. 
 */
static void _exit_process(const tc_error_t err) {
  syslog(LOG_INFO, "%s", tc_error_to_msg(err));
  closelog(); 
  exit(err);
}

/**
 * This is the signal hander we set on the daemon
 * process after initialization. This way, we can
 * intercept and handle signals from the OS.
 *
 * @param signal The signal from the OS.
 */
static void _signal_handler(const int signal) {
  switch(signal) {
    case SIGHUP:
      break;
    case SIGTERM:
      _exit_process(RECV_SIGTERM);
      break;
    default:
      syslog(LOG_INFO, "received unhandled signal");
  }
}

/**
 * When we start a daemon process, we need to fork from the
 * parent so we can appropriately configure the process
 * as a standalone, daemon process with approrpiate stdin,
 * stdout, and the like. Here, we handle errors if we are
 * unable to fork or we are the parent process and the fork
 * worked. If the fork failed, we record that and exit.
 * Otherwise, we exit the parent cleanly.
 *
 * @param pid The process ID of th enew process.
 */
static void _handle_fork(const pid_t pid) {
  // For some reason, we were unable to fork.
  if (pid < 0) {
    _exit_process(NO_FORK);
  }

  // Fork was successful so exit the parent process.
  if (pid > 0) {
    exit(OK);
  }
}

/**
 * Here, we handle the details of daemonizing a process.
 * This involves forking, opening the syslog connection,
 * configuring signal handling, and closing standard file
 * descriptors.
 */
static void _daemonize(void) {
  // Fork from the parent process.
  pid_t pid = fork();

  // Open syslog with the specified logmask.
  openlog(DAEMON_NAME, LOG_PID | LOG_NDELAY | LOG_NOWAIT, LOG_DAEMON);

  // Handle the results of the fork.
  _handle_fork(pid);

  // Now become the session leader.
  if (setsid() < -1) {
    _exit_process(NO_SETSID);
  }

  // Set our custom signal handling.
  signal(SIGTERM, _signal_handler);
  signal(SIGHUP, _signal_handler);

  // New file persmissions on this process, they need to be permissive.
  //umask(S_IRUSR | S_IWUSR | S_IRGRP | S_IWGRP | S_IROTH | S_IWOTH);
  //umask(666);

  // Change to the working directory.
  chdir(WORKING_DIR);

  // Closing file descriptors (STDIN, STDOUT, etc.).
  for (long x = sysconf(_SC_OPEN_MAX); x>=0; x--) {
    close(x);
  }
}

////resources
static void read_temp(void) {
    char *buffer = NULL;
    size_t size = 0;

    /* Open your_file in read-only mode */
    FILE *fp = fopen(TEMP_FILENAME, "r");
    fseek(fp, 0, SEEK_END);
    size = ftell(fp);
    rewind(fp);
    buffer = malloc((size + 1) * sizeof(*buffer)); 
    fread(buffer, size, 1, fp);
    buffer[size] = '\0';
    send_http_request(REPORT_URL, buffer, "POST", true);
}

static int write_state(char *state) {
    FILE *fp = fopen(STATE_FILENAME, "w");
    if (fp == NULL) {
        printf("unable to open file for writing\n");
        return ERR_WTF;
    }
    fputs(state, fp);
    fclose(fp);
    return OK;
}

// handle curl request to know if system should be on or off
static void handle_state_get(void) {
    // get commands from web server
    char *state = send_http_request(STATE_URL, NULL, "GET", false);
    if (strcmp(state, "true") == 0) {
        write_state("ON");
    } else if (strcmp(state, "false") == 0) {
        write_state("OFF");
    }

    //chunk.response = NULL;
    //chunk.size = NULL;
}

/*
// added 18July 2:03pm
// handle curl request to know if system should be on or off
static void handle_state_post(float temp, float morn_lo, float_hi) {
    // get commands from web server
    
    if (temp >= morn_hi){
      heater_state = OFF;
      char *state = send_http_request(STATE_URL, NULL, "POST", false);
      //write_state(&heater_state);
    }
    else if (temp <= morn_lo) {
      heater_state = ON;
      char *state = send_http_request(STATE_URL, NULL, "POST", true);
      //write_state(&heater_state);
    }
    

    //chunk.response = NULL;
    //chunk.size = NULL;
}
*/

////////////////////////////////////////////////////////////////////


/**
 * This runs the simulation. We essentially have a loop which
 * reads the heater state, adjust the temperature based on this
 * information, and writes the new temperature to the appropriate
 * location.
 */
static void _run_simulation(void) {

  // It's a bit cold! Note we're using a float in case we want to be
  // more sophisticated with the temperature management in the future.
  // Right now we just use a linear model.
  float temp = 84;
  char value[250]; 

  // morning hi/lo temps                         added 18July 7:34am
  float morn_lo = 68;
  float morn_hi = 72;
  tc_heater_state_t heater_state = OFF;         // moved from while loop, initial declaration
  write_state(&heater_state);                   // added 18July 1:43pm, saves heater state
  syslog(LOG_INFO, "beginning thermocouple simulation");
  while(true) {  
   
    //handle_state_get();
    // Read the heater state.   
    write_state(&heater_state);                  // added bing 18July 8:00am, pushing heater_state to state write prior to tc_read_state

        // toggle heat on or off before increment/decrement       added 18July 7:34am
    if (temp >= morn_hi){
      heater_state = OFF;
      write_state(&heater_state);
    }
    else if (temp <= morn_lo) {
      heater_state = ON;
      write_state(&heater_state);
    }
    
    
    tc_error_t err = tc_read_state(STATE_FILENAME, &heater_state);
    if (err != OK) _exit_process(err);



    // Is the heater on? then increase the temperature one degree.
    // Otherwise, it's getting colder!
    temp = (heater_state == ON) ? temp + 1 : temp - 1;
    gcvt(temp, 6, value);                           // converts float to string
    // Write the temp to the file.
    err = tc_write_temperature(TEMP_FILENAME, temp);
    send_http_request(TEMP_URL, value, "POST", true);
    if (err != OK) _exit_process(err);

    // Take a bit of a nap.
    sleep(SLEEP_DELAY);
  }
}

/**
 * A utility function to test for file existance.
 *
 * @param filename The name of the file to check.
 */
static bool _file_exists(const char* filename) {
  struct stat buffer;
  return (stat(filename, &buffer) == 0) ? true : false;
}

/**
 * A utility function to create a file.
 * 
 * @param name The name of the file to create.
 */
static void _create_file(const char* name) {
  FILE* fp = fopen(name, "w");
  if (fp == NULL) {
    _exit_process(NO_OPEN);
  }
  fclose(fp);
}

/**
 * When we first start up, the various files we need to
 * use may not exist. If that is the case, we create them 
 * here for future use.
 */
static void _configure(void) {
  if (!_file_exists(STATE_FILENAME)) {
    syslog(LOG_INFO, "no state file; creating.");
    _create_file(STATE_FILENAME);
  }

  if (!_file_exists(TEMP_FILENAME)) {
    syslog(LOG_INFO, "no temp file; creating.");
    _create_file(TEMP_FILENAME);
  }
  syslog(LOG_INFO, "test finished.");
}

static struct argp argp = {options, parse_opt, args_doc, doc};
/**
 * The daemon entry point.
 */
int main(int argc, char **argv) {
  int err;
    if (argc > 1) {
        // default arguments, which could be done in struct
        syslog(LOG_INFO, "Using command line rather than daemon script.");
        struct Arguments arguments;
        arguments.url = NULL;
        arguments.arg = NULL;
        arguments.post = false;
        arguments.get = false;
        arguments.put = false;
        arguments.delete = false;

        // parse the arguments
        argp_parse(&argp, argc, argv, 0, 0, &arguments);
    }
  // Daemonize the process.
  _daemonize();

  // Set up appropriate files if they don't exist.
  _configure();

  // Execute the primary daemon routines.
  _run_simulation();

  // If we get here, something weird has happened.
  return OK;
}
