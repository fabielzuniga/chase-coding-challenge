package com.fabiel.weather.core.util.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fabiel.weather.R;
import com.fabiel.weather.common.CommonProtos.QuantityProto;

import java.util.ArrayList;
import java.util.List;

/** A recycler view adapter that display a list of {@code QuantityProto} as properties. */
public final class QuantityRecyclerViewAdapter
    extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private final List<QuantityProto> data;

  /** Creates a new adapter. */
  public QuantityRecyclerViewAdapter() {
    this.data = new ArrayList<>();
  }

  /**
   * Sets the data to show by this adapter.
   *
   * @param data data
   */
  public void setData(List<QuantityProto> data) {
    this.data.clear();
    this.data.addAll(data);
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(
        R.layout.recycler_view_row_quantity, parent, false);
    return new RecyclerView.ViewHolder(view){};
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    QuantityProto quantity = this.data.get(position);
    TextView labelName = holder.itemView.findViewById(R.id.recycler_view_row_quantity_label_name);
    TextView labelValue = holder.itemView.findViewById(R.id.recycler_view_row_quantity_label_value);
    TextView labelUnit = holder.itemView.findViewById(R.id.recycler_view_row_quantity_label_unit);
    labelName.setText(quantity.getName());
    labelValue.setText(quantity.getValue());
    labelUnit.setText(quantity.getUnit());
  }

  @Override
  public int getItemCount() {
    return this.data.size();
  }
}
