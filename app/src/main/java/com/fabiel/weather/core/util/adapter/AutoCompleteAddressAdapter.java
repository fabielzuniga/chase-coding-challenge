package com.fabiel.weather.core.util.adapter;

import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fabiel.weather.R;
import com.fabiel.weather.core.util.GeocoderUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An adapter to be used with an AutoCompleteTextView that auto completes the address.
 */
public final class AutoCompleteAddressAdapter extends ArrayAdapter<Address>  {

  private final LayoutInflater layoutInflater;

  /**
   * Creates a new adapter.
   * @param context context
   */
  public AutoCompleteAddressAdapter (@NonNull Context context) {
    super(context, R.layout.spinner_row_address, new ArrayList<>());
    this.layoutInflater = LayoutInflater.from(context);
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    View view = convertView;
    if (view == null) {
      view = this.layoutInflater.inflate(R.layout.spinner_row_address, null, true);
    }
    Address address = getItem(position);
    TextView label = view.findViewById(R.id.spinner_row_address_label_address_text);
    label.setText(GeocoderUtil.addressToText(address));
    return view;
  }

  @Override
  public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    View view = convertView;
    if (view == null) {
      view = this.layoutInflater.inflate(R.layout.spinner_row_address, parent, false);
    }
    Address address = getItem(position);
    TextView label = view.findViewById(R.id.spinner_row_address_label_address_text);
    label.setText(GeocoderUtil.addressToText(address));
    return view;
  }

  @NonNull
  @Override
  public Filter getFilter() {
    return new AddressFilter(this);
  }

  private static class AddressFilter extends Filter {

    private final AutoCompleteAddressAdapter autocompleteAddressAdapter;

    AddressFilter(AutoCompleteAddressAdapter autocompleteAddressAdapter) {
      this.autocompleteAddressAdapter = autocompleteAddressAdapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
      FilterResults filterResults = new FilterResults();
      if (charSequence == null || charSequence.length() == 0) {
        filterResults.values = Collections.emptyList();
        filterResults.count = 0;
      } else {
        try {
          List<Address> addresses = GeocoderUtil.blockingFindAddressMatches(
              this.autocompleteAddressAdapter.getContext(), charSequence.toString());
          filterResults.values = addresses;
          filterResults.count = addresses.size();
        } catch (IOException e) {
          // TODO: Handle this error or use a logger instead of printing the stack trace
          e.printStackTrace();
        }
      }
      return filterResults;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
      this.autocompleteAddressAdapter.clear();
      if (filterResults.values != null) {
        this.autocompleteAddressAdapter.addAll((List<Address>) filterResults.values);
      }
      this.autocompleteAddressAdapter.notifyDataSetChanged();
    }

    @Override
    public CharSequence convertResultToString(Object resultValue) {
      return GeocoderUtil.addressToText((Address) resultValue);
    }
  }
}
