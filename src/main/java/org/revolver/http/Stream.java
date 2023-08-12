package org.revolver.http;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.progress.Progress;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.List;

public class Stream {
    static FFmpegExecutor EXECUTOR;

    public enum Type {
        MUSIC,
        VIDEO
    }

    public static List<String> getFiles(String path) {
        return FileUtils.listFiles(new File(path), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).stream().map(File::getAbsolutePath).toList();
    }

    public static FFmpegExecutor getExecutor() {
        try {
            if (EXECUTOR == null) {
                FFmpeg ffmpeg = new FFmpeg("/opt/homebrew/bin/ffmpeg");
                FFprobe ffprobe = new FFprobe("/opt/homebrew/bin/ffprobe");
                EXECUTOR = new FFmpegExecutor(ffmpeg, ffprobe);
            }

            return EXECUTOR;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static FFmpegBuilder getBuilder(List<String> files, int index, Type stream) {

        if (stream == Type.MUSIC) {
            return new FFmpegBuilder()
                    .setVerbosity(FFmpegBuilder.Verbosity.ERROR)
                    .readAtNativeFrameRate()
                    .setInput(files.get(index))
                    .addOutput("rtmp://localhost/live/stream")
                    .setFormat("flv")
                    .setAudioCodec("aac")
                    .setAudioSampleRate(44100)
                    .done();
        } else if (stream == Type.VIDEO) {
            return new FFmpegBuilder()
                    .setVerbosity(FFmpegBuilder.Verbosity.ERROR)
                    .readAtNativeFrameRate()
                    .setInput(files.get(index))
                    .addOutput("rtmp://localhost/live/stream")
                    .setFormat("flv")
                    .setAudioSampleRate(44100)
                    .setVideoCodec("libx264")
                    .setPreset("veryfast")
                    .done();
        }

        return null;
    }

    public static void music(List<String> files, int index) {
        start(files, index, Type.MUSIC);
    }

    public static void video(List<String> files, int index) {
        start(files, index, Type.VIDEO);
    }

    private static void start(List<String> files, int index, Type stream) {
        new Thread(() -> {
            try {
                FFmpegBuilder builder = getBuilder(files, index, stream);
                getExecutor().createJob(builder, progress -> {
                    if (progress.status == Progress.Status.END) {
                        Stream.next(files, index, stream).start();
                    }
                }).run();

            } catch (Exception e) {
                System.out.println("ERROR -- EXECUTOR IS NULL");
                Stream.next(files, index, stream).start();
            }
        }).start();
    }

    public static Thread next(List<String> files, int index, Type stream) {
        var channel = stream == Type.MUSIC ? "music" : "video";
        int next = index + 1 < files.size() ? index + 1 : 0;
        return new Thread(() -> {
            Client.sendFiles(files, next, channel);
        });
    }


}
