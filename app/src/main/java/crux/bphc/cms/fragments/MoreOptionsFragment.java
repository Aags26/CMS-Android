package crux.bphc.cms.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import crux.bphc.cms.R;

/**
 * This fragment adds support for a BottomSheet to show more options and should
 * be preffered over a normal dialog box.
 *
 * @author Abhijeet Viswa
 */
public class MoreOptionsFragment extends BottomSheetDialogFragment {

    private OptionsViewModel viewModel;
    private String header;
    private ArrayList<Option> options;

    public MoreOptionsFragment() {
    }

    /**
     * Create a new instance of the @code <code>MoreOptionsFragment</code>
     * @param header The header to be set to the dialog. Set to <code>null</code>
     *               or an empty string if no header is required.
     * @param options The list of options of that should be shown to the user.
     * @return An instance of <code>MoreOptionsFragment</code>
     */
    public static MoreOptionsFragment newInstance(String header, ArrayList<Option> options) {
        Bundle args = new Bundle();
        args.putString("header", header);
        args.putParcelableArrayList("options", options);
        MoreOptionsFragment fragment = new MoreOptionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            header = args.getString("header");
            options = args.getParcelableArrayList("options");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_more_options, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // obtain the view model
        viewModel = new ViewModelProvider(requireActivity()).get(OptionsViewModel.class);

        if (header != null && header.compareTo("") != 0) {
            ((TextView) view.findViewById(R.id.more_options_header)).setText(header);
        }

        // create the list
        ListView listView = view.findViewById(R.id.more_options_list);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(requireActivity(), R.layout.row_more_options) {
            OptionViewHolder vh;

            @Override
            public @NonNull
            View getView(int position, View convertView, @NotNull ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) requireActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.row_more_options, parent, false);
                    vh = new OptionViewHolder();
                    vh.text = convertView.findViewById(R.id.more_options_text);
                    vh.icon = convertView.findViewById(R.id.more_options_icon);
                    convertView.setTag(vh);
                } else {
                    vh = (OptionViewHolder) convertView.getTag();
                }

                Option option = options.get(position);
                vh.text.setText(option.optionText);
                if (option.drawableIcon != 0) {
                    vh.icon.setImageResource(option.drawableIcon);
                    vh.icon.setVisibility(View.VISIBLE);
                }
                return convertView;
            }
        };

        for (MoreOptionsFragment.Option option : options) {
            arrayAdapter.add(option.optionText);
        }
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            viewModel.setSelection(options.get(position));
            dismiss();
        });
    }

    /**
     * Wrapper class specifying an option.
     */
    public static class Option implements Parcelable {
        final int id;
        final String optionText;
        final int drawableIcon;

        /**
         * Constructor for a new <code>Option</code>
         * @param id a unique associated with this option
         * @param option_text the text to be shown to the user
         * @param drawable_icon resource id for a drawable that will appear as
         *                      the icon of the option
         */
        public Option(int id, String option_text, int drawable_icon) {
            this.id = id;
            this.optionText = option_text;
            this.drawableIcon = drawable_icon;
        }

        protected Option(Parcel in) {
            id = in.readInt();
            optionText = in.readString();
            drawableIcon = in.readInt();
        }

        public static final Creator<Option> CREATOR = new Creator<Option>() {
            @Override
            public Option createFromParcel(Parcel in) {
                return new Option(in);
            }

            @Override
            public Option[] newArray(int size) {
                return new Option[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeString(this.optionText);
            dest.writeInt(this.drawableIcon);
        }

        public int getId() {
            return id;
        }

    }

    /**
     *  <code>ViewModel</code> to observe selection events.
     */
    public static class OptionsViewModel extends ViewModel {
        private final MutableLiveData<Option> selection = new MutableLiveData<>();

        public OptionsViewModel() {
        }

        /**
         * Observe this <code>LiveData</code> for selection changes.
         */
        public LiveData<Option> getSelection() {
            return selection;
        }

        void setSelection(Option option) {
            selection.setValue(option);
        }

        /**
         * Clear the selection after observing it. Ensure to deregister the observer
         * so that you the observer is not called back again.
         */
        public void clearSelection() {
            selection.setValue(null);
        }

    }

    static class OptionViewHolder {
        private TextView text;
        private ImageView icon;
    }
}
