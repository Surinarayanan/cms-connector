package com.p3.content.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.PrintWriter;

/**
 * @Author : Suri Aravind @Creation Date : 16/02/24
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class WriterBean {
    private CMODTableType name;
    private String path;
    private PrintWriter printWriter;
    private boolean headerProcessed;
}
