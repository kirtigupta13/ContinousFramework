package com.cerner.devcenter.education.utils;

/**
 * Represents the enumeration used for completion rating for a particular
 * resource.
 *
 * @author Vinutha Nuchimaniyanda (VN046193)
 */
public enum CompletionRating {
    EXTREMELY_SATISFIED(4), SATISFIED(3), NEUTRAL(2), DISSATISFIED(1), EXTREMELY_DISSATISFIED(0);

    private int value;

    private CompletionRating(final int value) {
        this.value = value;
    }

    /**
     * Getter method that returns the integer value.
     *
     * @return a integer
     */
    public int getValue() {
        return value;
    }

    /**
     * Getter method that returns the enumeration constant value.
     *
     * @return {@link CompletionRating} corresponding to parameter value if
     *          within enum values ([0,4]). null otherwise.
     */
    public static CompletionRating getRating(final int value) {
        for (final CompletionRating completionRating : CompletionRating.values()) {
            if (completionRating.getValue() == value) {
                return completionRating;
            }
        }
        return null;
    }
}