/*
 * Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package jdk.nashorn.internal.objects;

import static jdk.nashorn.internal.runtime.ScriptRuntime.UNDEFINED;
import static jdk.nashorn.internal.runtime.linker.Lookup.MH;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import jdk.nashorn.internal.runtime.Property;
import jdk.nashorn.internal.runtime.PropertyMap;
import jdk.nashorn.internal.runtime.ScriptFunction;
import jdk.nashorn.internal.runtime.ScriptObject;
import jdk.nashorn.internal.runtime.linker.Lookup;

/**
 * Instances of this class serve as "prototype" object for script functions.
 * The purpose is to expose "constructor" property from "prototype". Also, nasgen
 * generated prototype classes extend from this class.
 *
 */
public class PrototypeObject extends ScriptObject {
    private static final PropertyMap nasgenmap$;

    private Object constructor;

    private static final MethodHandle GET_CONSTRUCTOR = findOwnMH("getConstructor", Object.class, Object.class);
    private static final MethodHandle SET_CONSTRUCTOR = findOwnMH("setConstructor", void.class, Object.class, Object.class);

    static {
        PropertyMap map = PropertyMap.newMap(PrototypeObject.class);
        map = Lookup.newProperty(map, "constructor", Property.NOT_ENUMERABLE, GET_CONSTRUCTOR, SET_CONSTRUCTOR);
        nasgenmap$ = map;
    }

    PrototypeObject() {
        this(nasgenmap$);
    }

    /**
     * PropertyObject constructor
     *
     * @param map property map
     */
    public PrototypeObject(final PropertyMap map) {
        super(map != nasgenmap$ ? map.addAll(nasgenmap$) : nasgenmap$);
        setProto(Global.objectPrototype());
    }

    PrototypeObject(final ScriptFunction func) {
        this();
        this.constructor = func;
    }

    /**
     * Get the constructor for this {@code PrototypeObject}
     * @param self self reference
     * @return constructor, probably, but not necessarily, a {@link ScriptFunction}
     */
    public static Object getConstructor(final Object self) {
        return (self instanceof PrototypeObject) ?
            ((PrototypeObject)self).getConstructor() :
            UNDEFINED;
    }

    /**
     * Reset the constructor for this {@code PrototypeObject}
     * @param self self reference
     * @param constructor constructor, probably, but not necessarily, a {@link ScriptFunction}
     */
    public static void setConstructor(final Object self, final Object constructor) {
        if (self instanceof PrototypeObject) {
            ((PrototypeObject)self).setConstructor(constructor);
        }
    }

    private Object getConstructor() {
        return constructor;
    }

    private void setConstructor(final Object constructor) {
        this.constructor = constructor;
    }

    private static MethodHandle findOwnMH(final String name, final Class<?> rtype, final Class<?>... types) {
        return MH.findStatic(MethodHandles.lookup(), PrototypeObject.class, name, MH.type(rtype, types));
    }
}