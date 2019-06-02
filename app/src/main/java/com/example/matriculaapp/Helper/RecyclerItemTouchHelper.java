package com.example.matriculaapp.Helper;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.example.matriculaapp.Adapter.CarreraAdapter;
import com.example.matriculaapp.Adapter.CursoAdapter;
import com.example.matriculaapp.Adapter.EstudianteAdapter;
import com.example.matriculaapp.Adapter.ProfesorAdapter;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback{

    private RecyclerItemTouchHelperListener listener;
    // Vistas
    private View foregroundView;
    private View backgroundViewEdit;
    private View backgroundViewDelete;
    private int dragColor = Color.rgb(102, 102, 255);

    // Constructor
    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    // Asignacion de banderas para la hora del movimiento
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END; // izq o derecha
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView,RecyclerView.ViewHolder viewHolder,RecyclerView.ViewHolder target) {
        // Posicion de origen, posicion final del item
        listener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if(viewHolder != null){
            if (this.listener.getClass().getSimpleName().equals("AdmEstudianteActivity")) {
                backgroundViewEdit = ((EstudianteAdapter.MyViewHolder) viewHolder).vistaEditar;
                backgroundViewDelete = ((EstudianteAdapter.MyViewHolder) viewHolder).vistaBorrar;
                foregroundView = ((EstudianteAdapter.MyViewHolder) viewHolder).vistaPrincipal;
            } else if (this.listener.getClass().getSimpleName().equals("AdmCarreraActivity")) {
                backgroundViewEdit = ((CarreraAdapter.MyViewHolder) viewHolder).vistaEditar;
                backgroundViewDelete = ((CarreraAdapter.MyViewHolder) viewHolder).vistaBorrar;
                foregroundView = ((CarreraAdapter.MyViewHolder) viewHolder).vistaPrincipal;
            } else if (this.listener.getClass().getSimpleName().equals("AdmProfesorActivity")) {
                backgroundViewEdit = ((ProfesorAdapter.MyViewHolder) viewHolder).vistaEditar;
                backgroundViewDelete = ((ProfesorAdapter.MyViewHolder) viewHolder).vistaBorrar;
                foregroundView = ((ProfesorAdapter.MyViewHolder) viewHolder).vistaPrincipal;
            } else if (this.listener.getClass().getSimpleName().equals("AdmCursoActivity")) {
                backgroundViewEdit = ((CursoAdapter.MyViewHolder) viewHolder).vistaEditar;
                backgroundViewDelete = ((CursoAdapter.MyViewHolder) viewHolder).vistaBorrar;
                foregroundView = ((CursoAdapter.MyViewHolder) viewHolder).vistaPrincipal;
            }

            //Selected item
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                //fancy color picked
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), Color.WHITE, dragColor);
                colorAnimation.setDuration(250); // milliseconds
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        foregroundView.setBackgroundColor((int) animator.getAnimatedValue());
                    }
                });
                colorAnimation.start();
            }
            getDefaultUIUtil().onSelected(foregroundView);
            super.onSelectedChanged(viewHolder, actionState);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    // Para limpiar la vista, se agregan animaciones y colores
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //clear view with fancy animation
        int color = Color.TRANSPARENT;
        Drawable background = foregroundView.getBackground();
        if (background instanceof ColorDrawable)
            color = ((ColorDrawable) background).getColor();
        //check color
        if (color == dragColor) {
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), dragColor, Color.WHITE);
            colorAnimation.setDuration(250); // milliseconds
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    foregroundView.setBackgroundColor((int) animator.getAnimatedValue());
                }
            });
            colorAnimation.start();
        }
        super.clearView(recyclerView, viewHolder);
        getDefaultUIUtil().clearView(foregroundView);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            drawBackground(dX);
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive);
        }
    }

    // Muestra los background segun se deslice el componente
    private void drawBackground(float dX) {
        if (this.listener.getClass().getSimpleName().equals("MatriculaActivity")) {
            backgroundViewDelete.setVisibility(View.VISIBLE);
        } else {
            if (dX > 0) {
                backgroundViewEdit.setVisibility(View.VISIBLE);
                backgroundViewDelete.setVisibility(View.GONE);
            } else {
                backgroundViewDelete.setVisibility(View.VISIBLE);
                backgroundViewEdit.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direccion) {
        listener.onSwiped(viewHolder, direccion, viewHolder.getAdapterPosition());
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    // SE CREA UNA INTERFAZ PARA UN LISTENER
    public interface RecyclerItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);

        void onItemMove(int source, int target);
    }

}
