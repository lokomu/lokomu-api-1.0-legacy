package no.delalt.back.model.dto.output;

public record RatingsDTO (long userAmount,
                          long sameConditionAmount,
                          long betterConditionAmount,
                          long worseConditionAmount,
                          long lateReturnAmount,
                          long notReturnedAmount){
}
