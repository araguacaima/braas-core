package com.araguacaima.braas.core;

import com.araguacaima.braas.core.exception.BraaSParserException;
import com.araguacaima.braas.core.exception.InternalBraaSException;
import com.araguacaima.commons.utils.EnumsUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Pattern;

import static com.araguacaima.braas.core.Commons.reflectionUtils;


public class SpreadsheetRuleUtils {

    private static final Logger log = LoggerFactory.getLogger(SpreadsheetRuleUtils.class);
    private static EnumsUtils enumsUtils = EnumsUtils.getInstance();
    private final String fieldSeparator;
    private final String headerSeparator;
    private final String matrixStr;
    private final String[] matrix;
    private final String[] fields;
    private final int objectSize;

    public SpreadsheetRuleUtils(String matrixStr, String fieldSeparator, String headerSeparator) throws InternalBraaSException {
        this.matrixStr = matrixStr;
        this.fieldSeparator = fieldSeparator;
        this.headerSeparator = headerSeparator;
        String[] strSplitted = matrixStr.split(Pattern.quote(headerSeparator));
        if (strSplitted.length != 2) {
            throw new BraaSParserException("It's not possible to parse incoming entry");
        }
        String headerStr = strSplitted[0];
        this.fields = headerStr.split(Pattern.quote(fieldSeparator));
        this.objectSize = fields.length;
        this.matrix = buildMatrix();
    }

    private static Object findById(Object newObject, Object lastObject, String field, Map<String, Collection<String>> idKeys) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (newObject == null || lastObject == null) {
            return null;
        }
        Collection<String> ids = idKeys.get(field);
        Object lastObject_ = lastObject;
        if (!newObject.getClass().equals(lastObject.getClass())) {
            lastObject_ = PropertyUtils.getProperty(lastObject, field);
        }
        if (reflectionUtils.isCollectionImplementation(lastObject_.getClass())) {
            Object[] orderedCollection = ((Collection) lastObject_).toArray(new Object[]{});
            lastObject_ = orderedCollection[orderedCollection.length - 1];
        }
        for (String id : ids) {
            Object newObjectValue = PropertyUtils.getProperty(newObject, id);
            Object lastObjectValue = PropertyUtils.getProperty(lastObject_, id);
            if (newObjectValue == null) {
                if (lastObject_.equals(newObject)) {
                    return null;
                }
                return lastObject_;
            } else {
                java.io.Serializable defaultValue = getDefaultValue(newObjectValue.getClass());
                if (newObjectValue.equals(defaultValue)) {
                    return lastObject_;
                } else {
                    return newObject;
                }
            }
        }
        return lastObject_;
    }

    private static Object traverseObject(String field, Class<?> parentType, Object parent, String value, Map<String, Collection<String>> idFields) throws InstantiationException, IllegalAccessException, InternalBraaSException, NoSuchMethodException, InvocationTargetException {
        String token;
        Field field_;
        if (field.contains(".")) {
            token = field.split("\\.")[0];
            field_ = reflectionUtils.getField(parentType, token);
            field_.setAccessible(true);
            Class childType = field_.getType();
            if (reflectionUtils.isCollectionImplementation(childType)) {
                Class innerType = reflectionUtils.extractGenerics(field_);
                String remainingField = field.replaceFirst(token + "\\.", StringUtils.EMPTY);
                Object innerObject;
                Collection col_ = (Collection) field_.get(parent);
                if (CollectionUtils.isNotEmpty(col_)) {
                    Object[] orderedCollection = col_.toArray(new Object[]{});
                    innerObject = orderedCollection[orderedCollection.length - 1];
                    Object newObject = innerType.newInstance();
                    PropertyUtils.setNestedProperty(newObject, remainingField, value);
                    Object existentObject = findById(newObject, innerObject, token, idFields);
                    if (existentObject == null || newObject.equals(existentObject)) {
                        innerObject = traverseObject(remainingField, innerType, innerObject, value, idFields);
                    } else {
                        if (existentObject.equals(newObject)) {
                            col_.add(newObject);
                            return newObject;
                        }
                    }
                } else {
                    innerObject = innerType.newInstance();
                    innerObject = traverseObject(remainingField, innerType, innerObject, value, idFields);
                    if (col_ == null) {
                        col_ = (Collection) reflectionUtils.deepInitialization(childType);
                    }
                    if (innerObject != null) {
                        col_.add(innerObject);
                    }
                    field_.set(parent, col_);
                }
                return innerObject;
            }
        } else {
            field_ = reflectionUtils.getField(parentType, field);
        }
        if (value != null) {
            Class<?> type = field_.getType();
            java.io.Serializable defaultValue = getDefaultValue(type);
            Object value_ = fixValue(type, value);
            if (value_ != null && !value_.equals(defaultValue)) {
                return buildObject(field, parentType, parent, value, null);
            }
        }
        return parent;
    }

    private static java.io.Serializable getDefaultValue(Class<?> targetType) {
        if (targetType.isEnum()) {
            return null;
        } else if (String.class.isAssignableFrom(targetType)) {
            return StringUtils.EMPTY;
        } else if (Boolean.class.isAssignableFrom(targetType) || Boolean.TYPE.isAssignableFrom(targetType)) {
            return false;
        } else if (Character.class.isAssignableFrom(targetType) || Character.TYPE.isAssignableFrom(targetType)) {
            return '\u0000';
        } else if (Short.class.isAssignableFrom(targetType) || Short.TYPE.isAssignableFrom(targetType)) {
            return 0;
        } else if (Integer.class.isAssignableFrom(targetType) || Integer.TYPE.isAssignableFrom(targetType)) {
            return 0;
        } else if (Long.class.isAssignableFrom(targetType) || Long.TYPE.isAssignableFrom(targetType)) {
            return 0L;
        } else if (Float.class.isAssignableFrom(targetType) || Float.TYPE.isAssignableFrom(targetType)) {
            return 0.0f;
        } else if (Double.class.isAssignableFrom(targetType) || Double.TYPE.isAssignableFrom(targetType)) {
            return 0.0d;
        }
        return null;
    }

    public String[] buildMatrix() throws InternalBraaSException {
        String[] matrix;
        try {
            String[] strSplitted = matrixStr.split(Pattern.quote(headerSeparator));
            if (strSplitted.length != 2) {
                throw new BraaSParserException("It's not possible to parse incoming entry");
            }
            String objectsStr = strSplitted[1];
            if (objectsStr.startsWith(fieldSeparator)) {
                objectsStr = objectsStr.replaceFirst(Pattern.quote(fieldSeparator), StringUtils.EMPTY);
            }
            int rowSize = fields.length;
            matrix = objectsStr.split(Pattern.quote(fieldSeparator));
            int matrixSize = matrix.length;
            for (int i = 0; i < rowSize; i++) {
                String previousValue = null;
                for (int j = 0; j < matrixSize; j++) {
                    int index = i + (j * rowSize);
                    if (index >= matrixSize) {
                        break;
                    }
                    String value = matrix[index];
                    if (StringUtils.isBlank(value)) {
                        matrix[index] = previousValue;
                    } else {
                        previousValue = value;
                    }
                }
            }
        } catch (Throwable t) {
            throw new InternalBraaSException(t);
        }
        return matrix;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public <T> Collection<T> stringArrayToBeans(String prefix, Class<?> clazz) throws InternalBraaSException {
        return stringArrayToBeans(prefix, clazz, null, null);
    }

    public static Object buildObject(String field, Class<?> fieldType, Object parent, Object value, String prefix) throws
            InternalBraaSException {
        Field field_;
        Object innerObject = null;
        if (StringUtils.isNotBlank(prefix) && field.startsWith(prefix)) {
            field = field.replace(prefix + ".", StringUtils.EMPTY);
        }
        String[] fieldTokens = field.split("\\.");
        try {
            if (fieldTokens.length > 1) {
                Object innerObject_;
                String token = fieldTokens[0];
                field_ = reflectionUtils.getField(fieldType, token);
                if (field_ != null) {
                    field_.setAccessible(true);
                    fieldType = field_.getType();
                    if (reflectionUtils.isCollectionImplementation(fieldType)) {
                        innerObject = reflectionUtils.deepInitialization(fieldType);
                        fieldType = reflectionUtils.extractGenerics(field_);
                        Object object_;
                        innerObject_ = fieldType.newInstance();
                        String remainingField = StringUtils.join(Arrays.copyOfRange(fieldTokens, 1, fieldTokens.length), ".");
                        object_ = buildObject(remainingField, fieldType, innerObject_, value, token);
                        field_.set(parent, innerObject);
                        ((Collection) innerObject).add(object_);
                        return object_;
                    } else {
                        innerObject = fieldType.newInstance();
                    }
                }
            } else {
                field_ = reflectionUtils.getField(fieldType, field);
                if (field_ != null) {
                    field_.setAccessible(true);
                    if (parent == null) {
                        innerObject = fieldType.newInstance();
                    } else {
                        innerObject = parent;
                    }
                    fieldType = field_.getType();
                }
            }
            return buildObject(fieldType, value, field_, innerObject);
        } catch (Throwable t) {
            throw new InternalBraaSException(t);
        }
    }

    private static Object buildObject(Class<?> fieldType, Object value, Field field_, Object innerObject) throws
            IllegalAccessException, InstantiationException {
        if (field_ != null) {
            Object cell = value;
            if (fieldType.isEnum()) {
                try {
                    String fieldName = cell.toString();
                    cell = enumsUtils.getEnum(fieldType, fieldName);
                } catch (Throwable t) {
                    log.trace("Unable to bind value of '" + cell + "' as type '" + String.class.getName() + "' for field '" + field_.toString() + "'");
                    cell = null;
                }
            } else if (String.class.isAssignableFrom(fieldType)) {
                try {
                    cell = cell.toString().trim();
                    if (StringUtils.EMPTY.equals(cell)) {
                        cell = null;
                    }
                } catch (Throwable t) {
                    log.trace("Unable to bind value of '" + cell + "' as type '" + String.class.getName() + "' for field '" + field_.toString() + "'");
                    cell = null;
                }
            } else if (Boolean.class.isAssignableFrom(fieldType) || Boolean.TYPE.isAssignableFrom(fieldType)) {
                String booleanValue = cell.toString().toLowerCase();
                try {
                    cell = Boolean.valueOf(booleanValue);
                } catch (Throwable t) {
                    log.trace("Unable to bind value of '" + cell + "' as type '" + Boolean.class.getName() + "' for field '" + field_.toString() + "'");
                    cell = false;
                }
            } else if (Character.class.isAssignableFrom(fieldType) || Character.TYPE.isAssignableFrom(fieldType)) {
                String characterValue = cell.toString();
                try {
                    cell = characterValue.charAt(0);
                } catch (Throwable t) {
                    log.trace("Unable to bind value of '" + cell + "' as type '" + Character.class.getName() + "' for field '" + field_.toString() + "'");
                    cell = '\u0000';
                }
            } else if (Short.class.isAssignableFrom(fieldType) || Short.TYPE.isAssignableFrom(fieldType)) {
                String booleanValue = cell.toString();
                try {
                    cell = Short.valueOf(booleanValue);
                } catch (Throwable t) {
                    log.trace("Unable to bind value of '" + cell + "' as type '" + Short.class.getName() + "' for field '" + field_.toString() + "'");
                    cell = 0;
                }
            } else if (Integer.class.isAssignableFrom(fieldType) || Integer.TYPE.isAssignableFrom(fieldType)) {
                String booleanValue = cell.toString();
                try {
                    cell = Integer.valueOf(booleanValue);
                } catch (Throwable t) {
                    log.trace("Unable to bind value of '" + cell + "' as type '" + Integer.class.getName() + "' for field '" + field_.toString() + "'");
                    cell = 0;
                }
            } else if (Long.class.isAssignableFrom(fieldType) || Long.TYPE.isAssignableFrom(fieldType)) {
                String booleanValue = cell.toString();
                try {
                    cell = Long.valueOf(booleanValue);
                } catch (Throwable t) {
                    log.trace("Unable to bind value of '" + cell + "' as type '" + Long.class.getName() + "' for field '" + field_.toString() + "'");
                    cell = 0L;
                }
            } else if (Float.class.isAssignableFrom(fieldType) || Float.TYPE.isAssignableFrom(fieldType)) {
                String booleanValue = cell.toString();
                try {
                    cell = Float.valueOf(booleanValue);
                } catch (Throwable t) {
                    log.trace("Unable to bind value of '" + cell + "' as type '" + Float.class.getName() + "' for field '" + field_.toString() + "'");
                    cell = 0.0f;
                }
            } else if (Double.class.isAssignableFrom(fieldType) || Double.TYPE.isAssignableFrom(fieldType)) {
                String booleanValue = cell.toString();
                try {
                    cell = Double.valueOf(booleanValue);
                } catch (Throwable t) {
                    log.trace("Unable to bind value of '" + cell + "' as type '" + Double.class.getName() + "' for field '" + field_.toString() + "'");
                    cell = 0.0d;
                }
            }
            if (innerObject == null) {
                innerObject = fieldType.newInstance();
            }
            field_.set(innerObject, cell);
        }
        return innerObject;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public LinkedList<Interval> getIntervals() throws InternalBraaSException {
        return getIntervals(null, null);
    }

    @SuppressWarnings("SpellCheckingInspection")
    public LinkedList<Interval> getIntervals(Integer start, Integer end) throws InternalBraaSException {
        LinkedList<Interval> subMatrixIndexes = new LinkedList<>();
        try {
            int matrixSize = end == null ? matrix.length : end;
            int i = start == null ? 0 : start;
            for (int j = 0; j < matrixSize; j++) {
                int index = i + (j * objectSize);
                if (index >= matrixSize) {
                    break;
                }
                String value = matrix[index];
                Optional<Interval> optionalInterval = subMatrixIndexes.stream().filter(element -> element.key.equals(value)).findFirst();
                if (!optionalInterval.isPresent()) {
                    subMatrixIndexes.add(new Interval(value, index, index + objectSize));
                } else {
                    Interval interval = optionalInterval.get();
                    if (interval.key.equals(value)) {
                        interval.end = index + objectSize - 1;
                    } else {
                        interval.end = index;
                    }
                }
            }
        } catch (Throwable t) {
            throw new InternalBraaSException(t);
        }
        return subMatrixIndexes;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public <T> Collection<T> stringArrayToBeans(String prefix, Class<?> clazz, Integer start, Integer end) throws InternalBraaSException {

        LinkedList<T> collectionOfObjects = new LinkedList<T>();
        try {
            int i = start == null ? 0 : start;
            int matrixSize = end == null ? matrix.length : end - i;
            String innerPrefix = prefix;

            String field = fields[i % objectSize];
            if (StringUtils.isNotBlank(innerPrefix)) {
                field = field.replaceFirst(Pattern.quote(innerPrefix + "."), StringUtils.EMPTY);
            }
            LinkedList<Interval> intervals = null;
            if (field.contains(".")) {
                String innerField = field.split("\\.")[0];
                innerPrefix = innerPrefix + "." + innerField;
            } else {
                intervals = getIntervals(i, matrixSize);
            }
            if (CollectionUtils.isNotEmpty(intervals)) {
                int previousIndex = 0;
                String previousValue = null;
                for (int j = 0; j < matrixSize; j++) {
                    int index = i + (j * objectSize);
                    if (index >= matrixSize) {
                        break;
                    }
                    String value = matrix[index];
                    String finalField = field;
                    Optional<T> obj = collectionOfObjects.stream().filter(element -> {
                        Field field_ = reflectionUtils.getField(clazz, finalField);
                        if (field_ != null) {
                            field_.setAccessible(true);
                            try {
                                Object result = field_.get(element);
                                if (result != null) {
                                    return result.equals(value);
                                }
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                        return false;
                    }).findFirst();
                    Object obj_;
                    if (!obj.isPresent()) {
                        previousIndex = index;
                        obj_ = clazz.newInstance();
                        PropertyUtils.setNestedProperty(obj_, field, value);
                        collectionOfObjects.add((T) obj_);
                        if (previousValue == null) {
                            previousValue = value;
                        }
                    } else {
                        previousValue = value;
                        processRow(matrix, previousIndex, index, collectionOfObjects.getLast(), innerPrefix, fields);
                    }
                }
            }
        } catch (Throwable t) {
            throw new InternalBraaSException(t);
        }
        return collectionOfObjects;
    }

    private void processRow(String[] matrix, int first, int last, Object obj, String prefix, String[] fields) throws
            IllegalAccessException, NoSuchMethodException, InvocationTargetException, InternalBraaSException, InstantiationException {
        first++;
        for (int j = first; j < last; j++) {
            String value = matrix[j];
            String field = fields[j];
            String originalField = field;
            String remainingField;
            if (StringUtils.isNotBlank(prefix)) {
                field = field.replaceFirst(Pattern.quote(prefix + "."), StringUtils.EMPTY);
            }
            if (field.contains(".")) {
                String field_ = field.split("\\.")[0];
                remainingField = field.substring(field_.length() + 1);
                field = field_;
            } else {
                remainingField = field;
            }
            Class<?> fieldType = obj.getClass();
            Field field_ = reflectionUtils.getField(fieldType, field);
            if (field_ != null) {
                field_.setAccessible(true);
                Class clazz = field_.getType();
                if (reflectionUtils.isCollectionImplementation(clazz)) {
                    Object innerObj = field_.get(obj);
                    if (innerObj == null) {
                        innerObj = reflectionUtils.deepInitialization(clazz);
                    }
                    Class innerClass = reflectionUtils.extractGenerics(field_);
                    String newPrefix = originalField.substring(0, originalField.length() - (remainingField.length() + 1));
                    LinkedList<Interval> intervals = getIntervals(j, null);
                    Collection innerObj1 = (Collection) innerObj;
                    for (Interval interval : intervals) {
                        if (interval.key.equals(value)) {
                            innerObj1.addAll(stringArrayToBeans(newPrefix, innerClass, interval.start, interval.end));
                        }
                    }
                } else {
                    traverseObject(remainingField, fieldType, obj, value, null);
                }
            }
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    public <
            T> Collection<T> stringArrayToBean2(String str, String prefix, Class<T> clazz, Map<String, Collection<String>> idFields) throws
            InternalBraaSException {

        LinkedList<T> collectionOfObjects = new LinkedList<T>();
        try {
            String[] strSplitted = str.split(Pattern.quote(headerSeparator));

            if (strSplitted.length != 2) {
                throw new BraaSParserException("It's not possible to parse incoming entry");
            }

            String headerStr = strSplitted[0];
            String objectsStr = strSplitted[1];

            if (objectsStr.startsWith(fieldSeparator)) {
                objectsStr = objectsStr.replaceFirst(Pattern.quote(fieldSeparator), StringUtils.EMPTY);
            }

            String[] fields = headerStr.split(Pattern.quote(fieldSeparator));
            int objectSize = fields.length;
            String[] matrix = objectsStr.split(Pattern.quote(fieldSeparator));
            int matrixSize = matrix.length;
            Object object = null;
            Class<?> fieldType = clazz;
            String innerPrefix = prefix;
            Object lastObject = null;
            for (int i = 0; i < matrixSize; i++) {
                if (object == null || ((i % objectSize) == 0)) {
                    object = clazz.newInstance();
                    if (!collectionOfObjects.isEmpty()) {
                        lastObject = collectionOfObjects.get(collectionOfObjects.size() - 1);
                    } else {
                        lastObject = object;
                    }
                    innerPrefix = prefix;
                    fieldType = clazz;
                }
                String field = fields[i % objectSize];
                String parentField;
                if (StringUtils.isNotBlank(innerPrefix)) {
                    String[] tokens = innerPrefix.split("\\.");
                    parentField = tokens[tokens.length - 1];
                    field = field.replaceFirst(Pattern.quote(innerPrefix + "."), StringUtils.EMPTY);
                    String[] tokens_ = field.split("\\.");
                    if (tokens_.length > 1) {
                        String token = tokens_[0];
                        parentField = token.equals(field) ? innerPrefix : token;
                    }
                } else {
                    parentField = clazz.getName();
                }
                String value = matrix[i];
                Object newObject = traverseObject(field, fieldType, object, value, idFields);
                if (newObject != null) {
                    Object existentObject;
                    existentObject = findById(newObject, object, parentField, idFields);
                    if (existentObject != null) {
                        newObject = existentObject;
                    } else {
                        newObject = lastObject;
                        object = newObject;
                    }
                    if (!newObject.equals(lastObject)) {
                        fieldType = newObject.getClass();
                        if (field.contains(".")) {
                            String innerField = field.split("\\.")[0];
                            innerPrefix = innerPrefix + "." + innerField;
                        }
                        object = newObject;
                    }
                }
                if (!IterableUtils.contains(collectionOfObjects, lastObject)) {
                    collectionOfObjects.add((T) lastObject);
                }
            }
        } catch (Throwable t) {
            throw new InternalBraaSException(t);
        }
        return collectionOfObjects;
    }

    private static Object fixValue(Class<?> targetType, String value) {
        if (targetType.isEnum()) {
            try {
                return enumsUtils.getEnum(targetType, value);
            } catch (Throwable t) {
                log.debug("Unable to bind value of '" + value + "' as type '" + String.class.getName() + "'");
            }
        } else if (String.class.isAssignableFrom(targetType)) {
            try {
                String newValue = value.trim();
                if (StringUtils.EMPTY.equals(value)) {
                    return null;
                }
                return newValue;
            } catch (Throwable t) {
                log.debug("Unable to bind value of '" + value + "' as type '" + String.class.getName() + "'");
            }
        } else if (Boolean.class.isAssignableFrom(targetType) || Boolean.TYPE.isAssignableFrom(targetType)) {
            String booleanValue = value.toLowerCase();
            try {
                return Boolean.valueOf(booleanValue);
            } catch (Throwable t) {
                log.debug("Unable to bind value of '" + value + "' as type '" + Boolean.class.getName() + "'");
                return false;
            }
        } else if (Character.class.isAssignableFrom(targetType) || Character.TYPE.isAssignableFrom(targetType)) {
            try {
                return value.charAt(0);
            } catch (Throwable t) {
                log.debug("Unable to bind value of '" + value + "' as type '" + Character.class.getName() + "'");
                return '\u0000';
            }
        } else if (Short.class.isAssignableFrom(targetType) || Short.TYPE.isAssignableFrom(targetType)) {
            try {
                return Short.valueOf(value);
            } catch (Throwable t) {
                log.debug("Unable to bind value of '" + value + "' as type '" + Short.class.getName() + "'");
                return 0;
            }
        } else if (Integer.class.isAssignableFrom(targetType) || Integer.TYPE.isAssignableFrom(targetType)) {
            try {
                return Integer.valueOf(value);
            } catch (Throwable t) {
                log.debug("Unable to bind value of '" + value + "' as type '" + Integer.class.getName() + "'");
                return 0;
            }
        } else if (Long.class.isAssignableFrom(targetType) || Long.TYPE.isAssignableFrom(targetType)) {
            try {
                return Long.valueOf(value);
            } catch (Throwable t) {
                log.debug("Unable to bind value of '" + value + "' as type '" + Long.class.getName() + "'");
                return 0L;
            }
        } else if (Float.class.isAssignableFrom(targetType) || Float.TYPE.isAssignableFrom(targetType)) {
            try {
                return Float.valueOf(value);
            } catch (Throwable t) {
                log.debug("Unable to bind value of '" + value + "' as type '" + Float.class.getName() + "'");
                return 0.0f;
            }
        } else if (Double.class.isAssignableFrom(targetType) || Double.TYPE.isAssignableFrom(targetType)) {
            try {
                return Double.valueOf(value);
            } catch (Throwable t) {
                log.debug("Unable to bind value of '" + value + "' as type '" + Double.class.getName() + "'");
                return 0.0d;
            }
        }
        return null;
    }

    public class Interval {
        private String key;
        private int start = 0;
        private int end = 0;

        public Interval(String key, int start, int end) {
            this.key = key;
            this.start = start;
            this.end = end;
        }

        public String getKey() {
            return key;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }
    }
}
