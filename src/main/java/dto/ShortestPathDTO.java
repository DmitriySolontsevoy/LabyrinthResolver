package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortestPathDTO {
    private List<String> path;
    private Integer cost;

    public void addCost(Integer additional) {
        cost += additional;
    }
}
