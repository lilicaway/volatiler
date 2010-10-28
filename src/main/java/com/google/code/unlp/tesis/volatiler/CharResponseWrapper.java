package com.google.code.unlp.tesis.volatiler;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class CharResponseWrapper extends HttpServletResponseWrapper {
    private final CharArrayWriter output;

    @Override
    public String toString() {
        return output.toString();
    }

    public CharResponseWrapper(HttpServletResponse response) {
        super(response);
        output = new CharArrayWriter();
    }

    @Override
    public PrintWriter getWriter() {
        return new PrintWriter(output);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        ServletOutputStream sos = new ServletOutputStream() {

            @Override
            public void write(int b) throws IOException {
                output.write(b);
            }
        };
        return sos;
    }
}