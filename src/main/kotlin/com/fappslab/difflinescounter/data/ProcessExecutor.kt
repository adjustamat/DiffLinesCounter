package com.fappslab.difflinescounter.data

import java.io.File

class ProcessExecutor {
   fun run(directory: File?, vararg command: String): Process {
      return ProcessBuilder(*command)
       .directory(directory)
       .start()
   }
}
