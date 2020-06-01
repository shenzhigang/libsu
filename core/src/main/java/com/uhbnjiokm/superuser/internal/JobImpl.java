/*
 * Copyright 2019 John "uhbnjiokm" Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uhbnjiokm.superuser.internal;

import com.uhbnjiokm.superuser.Shell;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class JobImpl extends Shell.Job {

    private List<String> out, err;
    private List<InputHandler> handlers;
    protected ShellImpl shell;
    private boolean redirect = false;

    JobImpl() {
        handlers = new ArrayList<>();
    }

    JobImpl(ShellImpl s) {
        this();
        shell = s;
    }

    private Shell.Result exec0() {
        if (out instanceof NOPList)
            out = new ArrayList<>();
        ResultImpl result = new ResultImpl();
        result.out = out;
        result.err = redirect ? out : err;
        Shell.Task task = shell.newTask(handlers, result);
        try {
            shell.execTask(task);
        } catch (IOException e) {
            if (e instanceof ShellTerminatedException) {
                return ResultImpl.SHELL_ERR;
            } else {
                InternalUtils.stackTrace(e);
                return ResultImpl.INSTANCE;
            }
        }
        if (redirect)
            result.err = null;
        return result;
    }

    @Override
    public Shell.Result exec() {
        return exec0();
    }

    @Override
    public void submit() {
        submit(null);
    }

    @Override
    public void submit(Shell.ResultCallback cb) {
        if (out instanceof NOPList && cb == null)
            out = null;
        shell.SERIAL_EXECUTOR.execute(() -> {
            Shell.Result result = exec0();
            if (cb != null)
                UiThreadHandler.run(() -> cb.onResult(result));
        });
    }

    @Override
    public Shell.Job to(List<String> output) {
        out = output;
        redirect = InternalUtils.hasFlag(Shell.FLAG_REDIRECT_STDERR);
        return this;
    }

    @Override
    public Shell.Job to(List<String> stdout, List<String> stderr) {
        out = stdout;
        err = stderr;
        redirect = false;
        return this;
    }

    @Override
    public Shell.Job add(InputStream in) {
        if (in != null)
            handlers.add(InputHandler.newInstance(in));
        return this;
    }

    @Override
    public Shell.Job add(String... cmds) {
        if (cmds != null && cmds.length > 0)
            handlers.add(InputHandler.newInstance(cmds));
        return this;
    }

}
