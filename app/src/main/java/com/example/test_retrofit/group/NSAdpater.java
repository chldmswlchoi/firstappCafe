package com.example.test_retrofit.group;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test_retrofit.R;
import com.example.test_retrofit.group.DTO.NSDTO;

import java.util.ArrayList;
import java.util.List;

public class NSAdpater extends RecyclerView.Adapter<NSAdpater.CafeViewHolder> {
    //위와 같은 형태로 해줘야함 extends RecyclerView.Adapter<NSAdpater.내가 만든 뷰홀더 클래스>
    //    즉 ReclyclerView.Adapter을 상속 받을 때 뷰홀더 타입을 지정해줘야 한다(꼭).
    //어댑터 클래스 생성시 꼭 필요한 것은 oncreateViewHolder,onBindViewHolder,getItemCount 메서드,
    //생성자, 사용자 정의 뷰홀더, 데이터 셋을 넣어줄 리스트

    List<NSDTO.NSItems> nsItems = new ArrayList<>();
    //전체 데이터  값이 담겨 있는 리스트
    //    Context context;

    public NSAdpater(List<NSDTO.NSItems> nsItems) {
        this.nsItems = nsItems;
    //  this.context = context;
    }
    //생성자를 만들어 줘야함 생성자에서 리스트를 통해 데이터세트들을 전달받음

    @NonNull
    @Override
    public CafeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e("1oncreate", "oncreate");
        View my_item = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_place_result_items, parent, false);
    //내가 만든 아이템 레이아웃을 클래스로 변환시켜주는 과정이다.
    //inflate 는 view를 실체 view 객체로 만들어준다.
        CafeViewHolder holder = new CafeViewHolder(my_item);
    //변환된 클래스는 내가 정의한 holder 클래스에 객체를 생성해 넘겨준다.
    //뷰홀더를 새로 생성해준다.
        Log.e("2oncreate", "oncreate");
        return holder;
    }
    //    리사이클러뷰는 뷰홀더를 새로 만들어야할 때마다 위 메서드를 호출한다.
    //    이 메서드는 뷰홀더와 그에 연결된 뷰를 생성하고 초기화하지만, 뷰의 내용을채우지 않는다.
    //    뷰홀더가 아직 특정 데이터에 바인딩된상태가 아니기 때문


    @Override
    public void onBindViewHolder(@NonNull CafeViewHolder holder, int position) {
        //        첫번째 매개 변수는 onCreateviewHolder 의 반환값 holder을 받음
        // position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
        Log.e("bind", "bind");

        NSDTO.NSItems nsItemsList = nsItems.get(position);
        //전체 데이터 값이 들어 있는 리스트에서 첫번째 요소의 값을 넣어준다.
        Log.e("뷰홀더 nsItems", String.valueOf(nsItems));
        Log.e("nsItemsList 뷰홀더", String.valueOf(nsItemsList));

        holder.tcafe.setText(Html.fromHtml(nsItemsList.getTitle()));
        Log.e("뷰홀더", nsItemsList.getTitle());

        holder.troad_address.setText(nsItemsList.getRoadAddress());
        Log.e("뷰홀더", nsItemsList.getRoadAddress());
//        뷰홀더에 데이터를 세팅해주는 과정정
    }
//    뷰홀더를 데이터와 연결할 때 이 메서드를 호출

    @Override
    //꼭 넣아주기 안 넣어줘서 오류 났었음
    public int getItemCount() {
        return nsItems.size();
    }
    // 이 메서드를 통해 데이터 세트의 크기를 가져올 수 있다.
    // 리사이클러뷰는 이 메서드를 사용해서 더 이상 표시할 수 있는 항목이 없을 때를 결정한다.
    //size와 length는 다름 size는 동적 length 정적


    interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public class CafeViewHolder extends RecyclerView.ViewHolder {

        protected TextView tcafe, troad_address;

        public CafeViewHolder(@NonNull View itemView) {
            //super()부모 생성자 호출
            super(itemView);
            Log.e("뷰홀더", "11111");
            tcafe = itemView.findViewById(R.id.cafe);
            troad_address = itemView.findViewById(R.id.road_address);
            Log.e("2222뷰홀더", "22222222");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
//                   getAdapterPosition()은 뷰홀더가 나타내는 항목의 어댑터 위치를 반환한다.
//                    단 아이템이 여전히 어댑터에 존재하는 경우에 어댑터의 위치이다.
//                    아이템이 어앱터에서 제거된경우  NO_POsition 반환
//                    RecyclerView.Adapter.notifyDataSetChanged()가 호출되었거
                    if (position != RecyclerView.NO_POSITION) {

                        if (mListener != null) {
                            mListener.onItemClick(view, position);
                        }
                    }
                }
            });

        }


    }

//    각 뷰를 보관하는 객체
}
