/*
 * (C) Copyright 2015 ETH Zurich Systems Group (http://www.systems.ethz.ch/) and others.
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
 *
 * Contributors:
 *     Markus Pilman <mpilman@inf.ethz.ch>
 *     Simon Loesing <sloesing@inf.ethz.ch>
 *     Thomas Etter <etterth@gmail.com>
 *     Kevin Bocksrocker <kevin.bocksrocker@gmail.com>
 *     Lucas Braun <braunl@inf.ethz.ch>
 */
package ch.ethz.tell;

import java.lang.reflect.Field;

public class Unsafe {
    public static sun.misc.Unsafe getUnsafe() {
        try {
            System.out.println("++++++++++++++++++++++++++++++");
            Field singleoneInstanceField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            System.out.println("++++++++++++++++++++++++++++++");
            singleoneInstanceField.setAccessible(true);
            return (sun.misc.Unsafe) singleoneInstanceField.get(null);
        } catch (IllegalArgumentException e) {
            throw new UnsafeException(e);
        } catch (SecurityException e) {
            throw new UnsafeException(e);
        } catch (NoSuchFieldException e) {
            System.out.println("++++++++++++++++++++++++++++++");
            System.out.println(sun.misc.Unsafe.class.getCanonicalName());
            System.out.println(sun.misc.Unsafe.class.getDeclaredFields()[0].getName());
            System.out.println("++++++++++++++++++++++++++++++");
            throw new UnsafeException(e);
        } catch (IllegalAccessException e) {
            throw new UnsafeException(e);
        }
    }
}

