#!/bin/sh

PRGDIR=`dirname "$0"`

[ -z "$TMTS_HOME" ] && TMTS_HOME=`cd "$PRGDIR" >/dev/null; pwd`

TMTS_LOG="$TMTS_HOME"/logs/server.log
TMTS_JAR="$TMTS_HOME"/lib/tmts.jar
TMTS_PID="$TMTS_HOME"/bin/tmts.pid

if [ -z "$JAVA_HOME" ]; then
  JAVA_PATH=`which java 2>/dev/null`
  if [ -n "$JAVA_PATH" ]; then
    JAVA_PATH=`dirname $JAVA_PATH 2>/dev/null`
    JAVA_HOME=`dirname $JAVA_PATH 2>/dev/null`
  fi
  if [ -z "$JAVA_HOME" ]; then
    if [ -x /usr/bin/java ]; then
      JAVA_HOME=/usr
      echo "$JAVA_HOME"
    fi
  fi
  if [ -z "$JAVA_HOME" ]; then
    echo "JAVA_HOME environment variable isn't defined. It's needed to run this program"
    exit 1
  fi
fi

if [ -z "$_JAVACMD" ]; then
  _JAVACMD="$JAVA_HOME"/bin/java
fi

JAVA_OPTS="-Dlogback.configurationFile=$TMTS_HOME/conf/logback.xml $JAVA_OPTS"

echo "Using TMTS_HOME:  $TMTS_HOME"
echo "Using JAVA_HOME:  $JAVA_HOME"
echo "Using JAVA_OPTS:  $JAVA_OPTS"

if [ ! -z "$TMTS_PID" ]; then
  if [ -f "$TMTS_PID" ]; then
    if [ -s "$TMTS_PID" ]; then
      echo "Existing PID file found during start."
      if [ -r "$TMTS_PID" ]; then
        PID=`cat "$TMTS_PID"`
        ps -p $PID >/dev/null 2>&1
        if [ $? -eq 0 ] ; then
          echo "TMTS server appears to still be running with PID $PID. Start aborted."
          exit 1
        else
          echo "Removing/clearing stale PID file."
          rm -f "$TMTS_PID" >/dev/null 2>&1
          if [ $? != 0 ]; then
            if [ -w "$TMTS_PID" ]; then
              cat /dev/null > "$TMTS_PID"
            else
              echo "Unable to remove or clear stale PID file. Start aborted."
              exit 1
            fi
          fi
        fi
      else
        echo "Unable to read PID file. Start aborted."
        exit 1
      fi
    else
      rm -f "$TMTS_PID" >/dev/null 2>&1
      if [ $? != 0 ]; then
        if [ ! -w "$TMTS_PID" ]; then
          echo "Unable to remove or write to empty PID file. Start aborted."
          exit 1
        fi
      fi
    fi
  fi
fi

mkdir -p "$TMTS_HOME/logs"
touch $TMTS_LOG
eval "\"$_JAVACMD\"" $JAVA_OPTS \
  -Dtmts.home="\"$TMTS_HOME\"" \
  -jar "\"$TMTS_JAR\"" \
  >> "$TMTS_LOG" 2>&1 "&"

result=$?
if [ $result -ne 0 ]; then
    echo "TMTS server started with error $result"
else
    echo $! > "$TMTS_PID"
    echo "TMTS server started with PID $!."
fi
exit $result
