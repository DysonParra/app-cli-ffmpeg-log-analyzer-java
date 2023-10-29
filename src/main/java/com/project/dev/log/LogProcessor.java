/*
 * @fileoverview    {LogProcessor}
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
package com.project.dev.log;

import com.project.dev.file.generic.FileProcessor;
import com.project.dev.flag.processor.Flag;
import com.project.dev.flag.processor.FlagMap;
import com.project.dev.struct.Video;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO: Description of {@code LogProcessor}.
 *
 * @author Dyson Parra
 * @since 11
 */
public class LogProcessor {

    private static final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SS");

    /**
     * TODO: Description of {@code calculateDuration}.
     *
     * @param line
     * @param video
     * @return
     */
    private static boolean calculateDuration(String line, Video video) {

        String regex = "\\s*Duration:.*?";
        if (video.getExpectDuration() != 0)
            regex = "frame=.*?time=.*?";

        if (line.matches(regex)) {
            Pattern pattern = Pattern.compile(".*?(\\d\\d:\\d\\d:\\d\\d.\\d\\d).*?");
            Matcher matcher = pattern.matcher(line);
            matcher.matches();
            String durationStr = matcher.group(1);

            float seconds = 0;
            try {
                Date date = dateFormat.parse(durationStr + "0");
                seconds = date.getTime() / 1000.0f;
            } catch (ParseException ex) {
                System.out.println("Error parsing: " + durationStr);
            }

            if (video.getExpectDuration() == 0) {
                video.setExpectDurationStr(durationStr);
                video.setExpectDuration(seconds);
            } else {
                video.setDurationStr(durationStr);
                video.setDuration(seconds);
            }
        }

        return true;
    }

    /**
     * TODO: Description of {@code processFlags}.
     *
     * @param flags
     * @return
     */
    public static boolean processFlags(Flag[] flags) {
        boolean result = false;
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Map<String, String> flagsMap = FlagMap.convertFlagsArrayToMap(flags);
        String logsPath = flagsMap.get("-logsPath");
        String outputPath = flagsMap.get("-outputPath");

        List<Video> videos = new ArrayList<>();
        List<File> files = new ArrayList<>();

        if (!FileProcessor.validatePath(logsPath)) {
            System.out.println("Invalid path in flag '-logsPath'");
            result = false;
        } else if (!FileProcessor.validatePath(outputPath)) {
            System.out.println("Invalid path in flag '-outputPath'");
            result = false;
        } else {
            FileProcessor.getFiles(new File(logsPath), new String[]{"log"}, files);

            for (File file : files) {
                System.out.println(file);
                Video video = Video.builder().path(file.getPath()).build();
                videos.add(video);
                result = FileProcessor.forEachLine(file.getPath(), LogProcessor::calculateDuration, video);
                video.setSecondsDiff(video.getExpectDuration() - video.getDuration());
            }
            try {
                try (FileWriter myWriter = new FileWriter(outputPath + "\\output.log")) {
                    for (Video video : videos) {
                        //System.out.println(video);
                        myWriter.write(video.toString() + "\n");
                    }
                }
                System.out.println("Successfully wrote to the file.");
            } catch (IOException e) {
                System.out.println("An error occurred.");
            }

        }
        return result;
    }

}
