package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeuristicNodeDTO {
    private String parentLabel;
    private Integer fromSourceCostValue;
    private Integer heuristicGoalDistance;

    public Integer getTotalValue() {
        return fromSourceCostValue + heuristicGoalDistance;
    }
}
