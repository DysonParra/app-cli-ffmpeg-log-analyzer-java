/*
 * @fileoverview    {Video}
 *
 * @version         2.0
 *
 * @author          Dyson Arley Parra Tilano <dysontilano@gmail.com>
 *
 * @copyright       Dyson Parra
 * @see             github.com/DysonParra
 *
 * History
 * @version 1.0     Implementation done.
 * @version 2.0     Documentation added.
 */
package com.project.dev.struct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO: Description of {@code Video}.
 *
 * @author Dyson Parra
 * @since 11
 */
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Video {

    String expectDurationStr;
    String durationStr;
    float expectDuration;
    float duration;
    float secondsDiff;
    String path;

    /**
     * TODO: Description of {@code toString}.
     *
     */
    @Override
    public String toString() {
        return String.format("%9.5f", secondsDiff) + "     " + expectDurationStr + "    " + durationStr + "     \"" + path + "\"";
    }

}
