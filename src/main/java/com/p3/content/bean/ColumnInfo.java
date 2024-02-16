package com.p3.content.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : Suri Aravind @Creation Date : 16/02/24
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

public class ColumnInfo {
    private String name;
    @Builder.Default private boolean primary = false;
    private int lineNo;
    private int position;
    private int length;
}
