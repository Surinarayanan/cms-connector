package com.p3.content.export.bean;

import java.util.List;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StructureBean {
    private String name;
    private CoortinateType type;
    private Coordinates coordinates;
    private List<StructureBean> nestedFields;
}
