package cgeo.geocaching.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import cgeo.geocaching.GpxFileListActivity;
import cgeo.geocaching.R;
import cgeo.geocaching.files.GPXImporter;
import cgeo.geocaching.ui.dialog.Dialogs;
import cgeo.geocaching.utils.FileUtils;
import cgeo.geocaching.utils.Log;

public class GPXListAdapter extends ArrayAdapter<File> {
    private final GpxFileListActivity activity;
    private final LayoutInflater inflater;

    protected static class ViewHolder extends AbstractViewHolder {
        @BindView(R.id.filepath) protected TextView filepath;
        @BindView(R.id.filename) protected TextView filename;

        public ViewHolder(final View view) {
            super(view);
        }
    }

    public GPXListAdapter(final GpxFileListActivity parentIn, final List<File> listIn) {
        super(parentIn, 0, listIn);

        activity = parentIn;
        inflater = ((Activity) getContext()).getLayoutInflater();
    }

    @Override
    public View getView(final int position, final View rowView, final ViewGroup parent) {
        if (position > getCount()) {
            Log.w("GPXListAdapter.getView: Attempt to access missing item #" + position);
            return null;
        }

        final File file = getItem(position);

        View view = rowView;

        final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.gpx_item, parent, false);
            holder = new ViewHolder(view);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                (new GPXImporter(activity, activity.getListId(), null)).importGPX(file);
            }
        });

        holder.filepath.setText(file.getParent());
        holder.filename.setText(file.getName());

        view.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(final View v) {
                Dialogs.confirmYesNo(activity, R.string.gpx_import_delete_title, activity.getString(R.string.gpx_import_delete_message, file.getName()), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int id) {
                        FileUtils.deleteIgnoringFailure(file);
                        GPXListAdapter.this.remove(file);
                    }
                });
                return true;
            }
        });

        return view;
    }
}
