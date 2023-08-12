package org.revolver.stream;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.progress.Progress;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.revolver.http.Client;

import java.io.File;
import java.util.List;

public class Streamer {

    public static List<String> getFiles(String path) {
        return FileUtils.listFiles(new File(path), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).stream().map(File::getAbsolutePath).toList();
    }

    public static FFmpegExecutor getExecutor() {
        FFmpegExecutor executor = null;
        try {
            FFmpeg ffmpeg = new FFmpeg("/opt/homebrew/bin/ffmpeg");
            FFprobe ffprobe = new FFprobe("/opt/homebrew/bin/ffprobe");
            executor = new FFmpegExecutor(ffmpeg, ffprobe);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return executor;
    }

    public static void play(int index, List<String> files) {
        new Thread(() -> {
            try {
                FFmpegBuilder builder = new FFmpegBuilder()
                        .setVerbosity(FFmpegBuilder.Verbosity.ERROR)
                        .readAtNativeFrameRate()
                        .setInput(files.get(index))
                        .addOutput("rtmp://localhost/live/stream")
                        .setFormat("flv")
                        .setAudioCodec("aac")
                        .setAudioSampleRate(44100)
                        .done();
                FFmpegExecutor executor = getExecutor();
                executor.createJob(builder, progress -> {
                    if (progress.status == Progress.Status.END) {
                        Streamer.next(index, files).start();
                    }
                }).run();

            } catch (Exception e) {
                System.out.println("ERROR -- EXECUTOR IS NULL");
                Streamer.next(index, files).start();
            }
        }).start();
    }

    public static Thread next(int index, List<String> files) {
        int next = index + 1 < files.size() ? index + 1 : 0;
        return new Thread(() -> {
            Client.sendFiles(next, files);
        });
    }


}
