package com.cerner.devcenter.education.utils;

/**
 * Enum of valid authorization levels.
 * 
 * @author Surbhi Singh(SS043472)
 */
public enum AuthorizationLevel {
    ADMIN(0), ASSOCIATE(1), INSTRUCTOR(2);

    private int level;

    /**
     * Get Enum Constant by its value
     * 
     * @return Enum constant corresponding to its value.
     * @throws IllegalArgumentException
     *             when {@value} is not valid
     */
    public static AuthorizationLevel getAuthorizationForValue(int level) throws IllegalArgumentException {
        for (AuthorizationLevel authLevel : values()) {
            if (authLevel.getLevel() == level) {
                return authLevel;
            }
        }
        throw new IllegalArgumentException("Not a valid Authorization Level" + level);
    }

    /**
     * Instantiation for enum
     * 
     * @param level
     */
    private AuthorizationLevel(int level) {
        this.level = Integer.valueOf(level);
    }

    /**
     * Level assigned to user profile.
     * 
     * @return Level corresponding to user role
     */
    public int getLevel() {
        return level;
    }
}
