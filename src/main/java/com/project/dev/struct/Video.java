/*
 * @fileoverview    {Video} se encarga de realizar tareas especificas.
 *
 * @version         2.0
 *
 * @author          Dyson Arley Parra Tilano <dysontilano@gmail.com>
 *
 * @copyright       Dyson Parra
 * @see             github.com/DysonParra
 *
 * History
 * @version 1.0     Implementación realizada el 11/09/2023.
 * @version 2.0     Documentación agregada el 11/09/2023.
 */
package com.project.dev.struct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO: Definición de {@code Video}.
 *
 * @author Dyson Parra
 * @since 1.8
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

    @Override
    public String toString() {
        return String.format("%9.5f", secondsDiff) + "     " + expectDurationStr + "    " + durationStr + "     \"" + path + "\"";
    }

}
