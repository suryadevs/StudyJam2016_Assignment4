package android.suryadevs.com.familyfinder;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Harshal Suryawanshi on 10-02-2016.
 */
public class FamilyAdapter extends ArrayAdapter<RowContent> {
    private final Activity context;
    private List<RowContent> objects;

    public FamilyAdapter(Activity context, List<RowContent> objects) {
        super(context, R.layout.item_layout, objects);
        this.context = context;
        this.objects = objects;
    }

    static class ViewHolder {           //holds references to the relevant views
        public ImageView imageView;     //image of the family member
        public TextView textView1;      //name of the family member
        public TextView textView2;      //relationship with me
        public Button locationButton;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder viewHolder = new ViewHolder();
        // rowView getting created for the first time
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_layout, null);
            // configure view holder
            viewHolder.imageView = (ImageView) rowView.findViewById(R.id.imageView);
            viewHolder.textView1 = (TextView) rowView.findViewById(R.id.textView);
            viewHolder.textView2 = (TextView) rowView.findViewById(R.id.textView2);
            viewHolder.locationButton = (Button) rowView.findViewById(R.id.button);
            rowView.setTag(viewHolder);
        }

        // for rowView created already
        viewHolder = (ViewHolder) rowView.getTag();
        //Getting Data from the Firebase Database
        Firebase myFirebaseRef = new Firebase("https://familylist-suryadevs.firebaseio.com/");
        //This lines keeps the local copy synced with the remote copy in firebase
        myFirebaseRef.keepSynced(true);
        final ViewHolder finalViewHolder = viewHolder;
        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // getting imagedURL from Firebase DB
                String imgUrl = String.valueOf(dataSnapshot.child("familyList").child(String.valueOf(position)).child("imageURL").getValue());
               // Log.d("getImgFromFirebaseDb: ", imgUrl);
                Picasso.with(getContext()).load(imgUrl).into(finalViewHolder.imageView);
                //getting name from object from Firebase DB
                String personName = String.valueOf(dataSnapshot.child("familyList").child(String.valueOf(position)).child("name").getValue());
              //  Log.d("getNameFromFirebaseDb: ", personName);
                finalViewHolder.textView1.setText(personName);
                //Getting relationship from Firebase DB
                String personRelation = String.valueOf(dataSnapshot.child("familyList").child(String.valueOf(position)).child("relation").getValue());
                //Log.d("getRelationFromFbDb: ", personRelation);
                finalViewHolder.textView2.setText(personRelation);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        viewHolder.locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("clicked", "button");
                intent.putExtra("position",position);;
                context.startActivity(intent);
            }
        });

        return rowView;

    }
}
