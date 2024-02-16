package com.p3.content.export.bean;

/**
 * @Author : Suri Aravind @Creation Date : 16/02/24
 */
import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class TemplateConfigBean {
    private String template;
    private Coordinates table_coordinates;
    private long totalLines;
    private List<StructureBean> fields;
}
