package org.jkiss.dbeaver.debug.core.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.osgi.util.NLS;
import org.jkiss.dbeaver.debug.DBGException;
import org.jkiss.dbeaver.debug.DBGStackFrame;
import org.jkiss.dbeaver.debug.DBGVariable;

public class DatabaseStackFrame extends DatabaseDebugElement implements IStackFrame {
    
    private static final IRegisterGroup[] NO_REGISTER_GROUPS = new IRegisterGroup[0];
    private static final IVariable[] NO_VARIABLES = new IVariable[0];

    private final List<DatabaseVariable> variables = new ArrayList<DatabaseVariable>();
    
    private final DatabaseThread thread;
    private final DBGStackFrame dbgStackFrame;

    private boolean refreshVariables = true;

    public DatabaseStackFrame(DatabaseThread thread, DBGStackFrame dbgStackFrame) {
        super(thread.getDatabaseDebugTarget());
        this.thread = thread;
        this.dbgStackFrame = dbgStackFrame;
    }

    @Override
    public boolean canStepInto() {
        return getThread().canStepInto();
    }

    @Override
    public boolean canStepOver() {
       return getThread().canStepOver();
    }

    @Override
    public boolean canStepReturn() {
        return getThread().canStepReturn();
    }

    @Override
    public boolean isStepping() {
        return getThread().isStepping();
    }

    @Override
    public void stepInto() throws DebugException {
        getThread().stepInto();
    }

    @Override
    public void stepOver() throws DebugException {
        getThread().stepOver();
    }

    @Override
    public void stepReturn() throws DebugException {
        getThread().canStepReturn();
    }

    @Override
    public boolean canResume() {
        return getThread().canResume();
    }

    @Override
    public boolean canSuspend() {
        return getThread().canSuspend();
    }

    @Override
    public boolean isSuspended() {
        return getThread().isSuspended();
    }

    @Override
    public void resume() throws DebugException {
        getThread().resume();
    }

    @Override
    public void suspend() throws DebugException {
        getThread().suspend();
    }

    @Override
    public boolean canTerminate() {
        return getThread().canTerminate();
    }

    @Override
    public boolean isTerminated() {
        return getThread().isTerminated();
    }

    @Override
    public void terminate() throws DebugException {
        getThread().terminate();
    }

    @Override
    public IThread getThread() {
        return thread;
    }

    @Override
    public IVariable[] getVariables() throws DebugException {
        if (refreshVariables) {
            try {
                List<? extends DBGVariable<?>> variables = getDatabaseDebugTarget().requestVariables();
                rebuildVariables(variables);
            } catch (DBGException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (variables.isEmpty()) {
            return NO_VARIABLES;
        }
        return (IVariable[]) variables.toArray(new IVariable[variables.size()]);
    }
    
    protected void invalidateVariables() {
        refreshVariables = true;
    }

    protected void rebuildVariables(List<? extends DBGVariable<?>> dbgVariables) {
        try {
            variables.clear();
            for (DBGVariable<?> dbgVariable : dbgVariables) {
                DatabaseVariable variable = new DatabaseVariable(getDatabaseDebugTarget(), dbgVariable);
                variables.add(variable);
            }
        } finally {
            refreshVariables = false;
        }
    }

    @Override
    public boolean hasVariables() throws DebugException {
        return isSuspended();
    }

    @Override
    public int getLineNumber() throws DebugException {
        return dbgStackFrame.getLine();
    }

    @Override
    public int getCharStart() throws DebugException {
        // unknown
        return -1;
    }

    @Override
    public int getCharEnd() throws DebugException {
        // unknown
        return -1;
    }

    @Override
    public String getName() throws DebugException {
        String pattern = "{0} line: {1}";
        String name = NLS.bind(pattern, dbgStackFrame.getName(), dbgStackFrame.getLine());
        return name;
    }

    @Override
    public IRegisterGroup[] getRegisterGroups() throws DebugException {
        return NO_REGISTER_GROUPS;
    }

    @Override
    public boolean hasRegisterGroups() throws DebugException {
        return false;
    }

}
