package com.cerner.devcenter.education.helpers;

/**
 * This class is responsible for calculating correct number of pages required to
 * display resources per particular category.
 * 
 * When total number of resources is divided by resources per page, value is
 * round off to lower integer value. This class rounds to higher integer value
 * and returns it.
 * 
 * @author Naga Rishyendar Panguluri(NP046332)
 * 
 */
public class RoundToHigherIntHelper {

    /**
     * Calculates number of pages required to display resources per particular
     * category.
     *
     * @param resourcesCount
     *            Total number of resources per particular category.
     * @param resourcesLimit
     *            Maximum number of resources could be visible per page.
     * @return correct number of pages required to display resources for
     *         particular category.
     */
    public static int roundToHigherInt(int resourcesCount, int resourcesLimit) {
        int pageCountRemainder = (resourcesCount % resourcesLimit);
        int pageCount = (resourcesCount / resourcesLimit);
        if (pageCountRemainder != 0) {
            pageCount++;
        }
        return pageCount;
    }

}
