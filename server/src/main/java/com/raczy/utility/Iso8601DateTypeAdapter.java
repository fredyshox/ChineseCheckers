package com.raczy.utility;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Utility class that enables GSON to convert dates to millisecond unix timestamp format
 * Created by kacperraczy on 03.01.2018.
 */
public class Iso8601DateTypeAdapter extends TypeAdapter<Date> {

    private final DateFormat iso8601Format;

    public Iso8601DateTypeAdapter(){
        this.iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        this.iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public void write(JsonWriter jsonWriter, Date date) throws IOException {
        if (date == null) {
            jsonWriter.nullValue();
        }else {
            jsonWriter.value(this.iso8601Format.format(date));
        }
    }

    @Override
    public Date read(JsonReader jsonReader) throws IOException {
        try {
            if (jsonReader != null) {
                return iso8601Format.parse(jsonReader.nextString());
            }
        }catch (ParseException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
