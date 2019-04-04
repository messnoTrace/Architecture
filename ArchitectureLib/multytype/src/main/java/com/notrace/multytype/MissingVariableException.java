package com.notrace.multytype;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;

class MissingVariableException extends IllegalStateException{
    public MissingVariableException(ViewDataBinding binding, int bindingVariable, @LayoutRes int layoutRes) {
        super("Could not bind variable '" + getBindingVariableName(bindingVariable) + "' in layout '" + getLayoutName(binding, layoutRes) + "'");
    }

    private static String getBindingVariableName(int bindingVariable) {
        String bindingVariableName = DataBindingUtil.convertBrIdToString(bindingVariable);
        return bindingVariableName;
    }

    private static String getLayoutName(ViewDataBinding binding, @LayoutRes int layoutRes) {
        Context context = binding.getRoot().getContext();
        Resources resources = context.getResources();
        String layoutName = resources.getResourceName(layoutRes);
        return layoutName;
    }
}
