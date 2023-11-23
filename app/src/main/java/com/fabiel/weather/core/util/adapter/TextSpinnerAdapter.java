package com.fabiel.weather.core.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fabiel.weather.R;

import java.util.List;
import java.util.function.Function;

/**
 * An Spinner adapter that displays data in a single label.
 * @param <T> type of the data
 */
public final class TextSpinnerAdapter<T> extends ArrayAdapter<T> {

  private final LayoutInflater layoutInflater;
  private final Function<T, String> dataFormatter;

  public TextSpinnerAdapter(
      @NonNull Context context,
      @NonNull List<T> data,
      Function<T, String> dataFormatter) {
    super(context, R.layout.spinner_row_single_label, data);
    this.layoutInflater = LayoutInflater.from(context);
    this.dataFormatter = dataFormatter;
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    View view = convertView;
    if (view == null) {
      view = this.layoutInflater.inflate(R.layout.spinner_row_single_label, null, true);
    }
    T item = getItem(position);
    TextView label = view.findViewById(R.id.single_label_spinner_row_label);
    label.setText(dataFormatter.apply(item));
    return view;
  }

  @Override
  public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    View view = convertView;
    if (view == null) {
      view = this.layoutInflater.inflate(R.layout.spinner_row_single_label, parent, false);
    }
    T item = getItem(position);
    TextView label = view.findViewById(R.id.single_label_spinner_row_label);
    label.setText(dataFormatter.apply(item));
    return view;
  }
}
