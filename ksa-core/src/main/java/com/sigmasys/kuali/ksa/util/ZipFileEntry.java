package com.sigmasys.kuali.ksa.util;

/**
 * An entry in a Zip file.
 * @author Sergey
 */
public class ZipFileEntry {
    //Contents
    private byte[] content;
    //Entry name
    private String name;

    public ZipFileEntry(byte[] content, String name) {
        this.content = content;
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
