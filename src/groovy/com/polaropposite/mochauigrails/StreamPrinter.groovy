package com.polaropposite.mochauigrails

// Helper class for redirecting output of process
class StreamPrinter extends Thread {
    InputStream inputStream

    StreamPrinter(InputStream is) {
        this.inputStream = is
    }

    public void run() {
        new BufferedReader(new InputStreamReader(inputStream)).withReader {reader ->
            String line
            while ((line = reader.readLine()) != null) {
                println(line)
            }
        }
    }
}