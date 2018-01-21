#!/bin/sh

PRGDIR=`dirname "$0"`

[ -z "$TMTS_HOME" ] && TMTS_HOME=`cd "$PRGDIR" >/dev/null; pwd`

TMTS_PID="$TMTS_HOME"/bin/tmts.pid

if [ -f "$TMTS_PID" ]; then
  if [ -s "$TMTS_PID" ]; then
    kill -0 `cat "$TMTS_PID"` >/dev/null 2>&1
    if [ $? -gt 0 ]; then
      echo "PID file found but no matching process was found. Stop aborted."
      exit 1
    fi
  else
    echo "PID file is empty and has been ignored."
  fi
else
  echo "PID file $TMTS_PID does not exist. Is TMTS server running? Stop aborted."
  exit 1
fi

PID=`cat "$TMTS_PID"`
echo "Stopping TMTS server with the PID: $PID"
kill -15 $PID >/dev/null 2>&1

FORCE_KILL=1
TERM_SLEEP_INTERVAL=5
if [ -f "$TMTS_PID" ]; then
  while [ $TERM_SLEEP_INTERVAL -ge 0 ]; do
    kill -0 $PID >/dev/null 2>&1
    if [ $? -gt 0 ]; then
      rm -f "$TMTS_PID" >/dev/null 2>&1
      if [ $? != 0 ]; then
        if [ -w "$TMTS_PID" ]; then
          cat /dev/null > "$TMTS_PID"
          FORCE_KILL=0
        else
          echo "The PID file could not be removed or cleared."
        fi
      fi
      echo "TMTS server stopped."
      break
    fi
    if [ $TERM_SLEEP_INTERVAL -gt 0 ]; then
      sleep 1
    fi
    TERM_SLEEP_INTERVAL=`expr $TERM_SLEEP_INTERVAL - 1 `
  done
fi

KILL_SLEEP_INTERVAL=5
if [ $FORCE_KILL -eq 1 ]; then
  if [ -f "$TMTS_PID" ]; then
    echo "Killing TMTS server with the PID: $PID"
    kill -9 $PID >/dev/null 2>&1
    while [ $KILL_SLEEP_INTERVAL -ge 0 ]; do
        kill -0 $PID >/dev/null 2>&1
        if [ $? -gt 0 ]; then
            rm -f "$TMTS_PID" >/dev/null 2>&1
            if [ $? != 0 ]; then
                if [ -w "$TMTS_PID" ]; then
                    cat /dev/null > "$TMTS_PID"
                else
                    echo "The PID file could not be removed."
                fi
            fi
            KILL_SLEEP_INTERVAL=0
            echo "The TMTS server process has been killed."
            break
        fi
        if [ $KILL_SLEEP_INTERVAL -gt 0 ]; then
            sleep 1
        fi
        KILL_SLEEP_INTERVAL=`expr $KILL_SLEEP_INTERVAL - 1 `
    done
    if [ $KILL_SLEEP_INTERVAL -gt 0 ]; then
        echo "TMTS server has not been killed completely yet. The process might be waiting on some system call or might be UNINTERRUPTIBLE."
    fi
  fi
fi