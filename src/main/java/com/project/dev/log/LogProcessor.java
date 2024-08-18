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
     * TODO: Description of {@code getVideos}.
     *
     * @param line
     * @param ffmpegVideos
     * @return
     */
    private static boolean addVideoPath(String line, List<String> ffmpegVideos) {

        String regex = "Output file:\\s*.*\\\\(.*?)";

        if (line.matches(regex)) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(line);
            matcher.matches();
            String path = matcher.group(1);
            //System.out.println("'" + path + "'");
            ffmpegVideos.add(path + ".log");
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
        String ffmpegLogFilePath = flagsMap.get("-ffmpegLogFilePath");
        String outputPath = flagsMap.get("-outputPath");
        String outputDurationsFile = "durations.log";
        String outputComparedFile = "comparation.log";

        List<Video> videos = new ArrayList<>();
        List<String> ffmpegFiles = new ArrayList<>();
        List<String> logFiles = new ArrayList<>();
        List<String> onlylogFiles = new ArrayList<>();
        List<String> onlyFfmpegFiles = new ArrayList<>();
        List<File> files = new ArrayList<>();

        if (!FileProcessor.validatePath(logsPath)) {
            System.out.println("Invalid path in flag '-logsPath'");
            result = false;
        } else if (!FileProcessor.validatePath(outputPath)) {
            System.out.println("Invalid path in flag '-outputPath'");
            result = false;
        } else {
            result = FileProcessor.forEachLine(ffmpegLogFilePath, LogProcessor::addVideoPath, ffmpegFiles);
            FileProcessor.getFiles(new File(logsPath), new String[]{"log"}, files);
            for (File file : files) {
                System.out.println(file.getName());
                logFiles.add(file.getName());
                Video video = Video.builder().path(file.getName()).build();
                videos.add(video);
                result = FileProcessor.forEachLine(file.getPath(), LogProcessor::calculateDuration, video);
                video.setSecondsDiff(video.getExpectDuration() - video.getDuration());
            }

            try {
                try (FileWriter myWriter = new FileWriter(outputPath + "\\" + outputDurationsFile)) {
                    for (Video video : videos) {
                        //System.out.println(video);
                        myWriter.write(video.toString() + "\n");
                    }
                }
                System.out.println(String.format("Successfully wrote to the file '%s'", outputDurationsFile));
            } catch (IOException e) {
                System.out.println("An error occurred.");
            }
            for (String file : ffmpegFiles)
                if (!logFiles.contains(file))
                    onlyFfmpegFiles.add(file);

            for (String file : logFiles)
                if (!ffmpegFiles.contains(file))
                    onlylogFiles.add(file);

            System.out.println("");
            System.out.println("Only in Ffmpeg: " + onlyFfmpegFiles.size());
            System.out.println("Only log File:  " + onlylogFiles.size());

            try {
                try (FileWriter myWriter = new FileWriter(outputPath + "\\" + outputComparedFile)) {
                    myWriter.write("Only in ffmpeg.log:\n");
                    for (String file : onlyFfmpegFiles) {
                        //System.out.println(file);
                        myWriter.write(file + "\n");
                    }

                    myWriter.write("\n\nOnly exists the log file:\n");
                    for (String file : onlylogFiles) {
                        //System.out.println(file);
                        myWriter.write(file + "\n");
                    }
                }
                System.out.println(String.format("Successfully wrote to the file '%s'", outputComparedFile));
            } catch (IOException e) {
                System.out.println("An error occurred.");
            }
        }
        return result;
    }

}
