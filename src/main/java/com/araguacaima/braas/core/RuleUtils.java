package com.araguacaima.braas.core;

import com.araguacaima.braas.core.exception.BraaSParserException;
import com.araguacaima.braas.core.exception.InternalBraaSException;
import com.araguacaima.commons.utils.EnumsUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

import static com.araguacaima.braas.core.Commons.reflectionUtils;


public class RuleUtils {

    private static final Logger log = LoggerFactory.getLogger(RuleUtils.class);
    private static EnumsUtils enumsUtils = EnumsUtils.getInstance();

    public static Map buildMatrix(String matrixStr) {
        String[] matrixSplitted = matrixStr.split(",");
        Map<String, String> result = new LinkedHashMap<>();
        int length = (matrixSplitted.length / 2);
        String[] keys = Arrays.copyOfRange(matrixSplitted, 0, length);
        String[] values = Arrays.copyOfRange(matrixSplitted, length, matrixSplitted.length);
        for (int i = 0; i < length; i++) {
            result.put(keys[i], values[i]);
        }
        return result;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static <T> Collection<T> buildCollectionOfObjects(String str, String fieldSeparator, String headerSeparator, String prefix, Class<T> clazz, Map<String, Collection<String>> idFields) throws InternalBraaSException {

        Collection<T> collectionOfObjects = new LinkedHashSet<>();
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
                String field = fields[i % objectSize];
                if (StringUtils.isNotBlank(prefix)) {
                    field = field.replaceFirst(Pattern.quote(innerPrefix), StringUtils.EMPTY);
                }
                if (field.startsWith(".")) {
                    field = field.substring(1);
                }

                if (object == null || ((i % objectSize) == 0)) {
                    object = clazz.newInstance();
                    lastObject = object;
                    collectionOfObjects.add((T) object);
                    innerPrefix = prefix;
                }
                String value = matrix[i];
                Object newObject = traverseObject(field, fieldType, object, value);
                if (newObject != null && !newObject.equals(lastObject)) {
                    fieldType = newObject.getClass();
                    if (field.contains(".")) {
                        String innerField = field.split("\\.")[0];
                        innerPrefix = innerPrefix + "." + innerField;
                    }
                    object = newObject;
                }
            }
        } catch (Throwable t) {
            throw new InternalBraaSException(t);
        }
        return collectionOfObjects;
    }

    private static Object traverseObject(String field, Class<?> parentType, Object parent, String value) throws InstantiationException, IllegalAccessException, InternalBraaSException {
        String token;
        if (field.contains(".")) {
            token = field.split("\\.")[0];
            Field field_ = reflectionUtils.getField(parentType, token);
            field_.setAccessible(true);
            Class childType = field_.getType();
            if (reflectionUtils.isCollectionImplementation(childType)) {
                Class innerType = reflectionUtils.extractGenerics(field_);
                String remainingField = field.replaceFirst(token + "\\.", StringUtils.EMPTY);
                Object innerObject = innerType.newInstance();
                innerObject = traverseObject(remainingField, innerType, innerObject, value);
                Object col = reflectionUtils.deepInitialization(childType);
                ((Collection) col).add(innerObject);
                field_.set(parent, col);
                return innerObject;
            }
        }
        return buildObject(field, parentType, parent, value, null);
    }

    public static Object buildObject(String field, Class<?> fieldType, Object parent, Object value, String prefix) throws InternalBraaSException {
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

    private static Object buildObject(Class<?> fieldType, Object value, Field field_, Object innerObject) throws IllegalAccessException, InstantiationException {
        if (field_ != null) {
            Object cell = value;
            if (fieldType.isEnum()) {
                try {
                    String fieldName = cell.toString();
                    cell = enumsUtils.getEnum(fieldType, fieldName);
                } catch (Throwable t) {
                    log.debug("Unable to bind value of '" + cell + "' as type '" + String.class.getName() + "' for field '" + field_.toString() + "'");
                    cell = null;
                }
            } else if (String.class.isAssignableFrom(fieldType)) {
                try {
                    cell = cell.toString().trim();
                    if (StringUtils.EMPTY.equals(cell)) {
                        cell = null;
                    }
                } catch (Throwable t) {
                    log.debug("Unable to bind value of '" + cell + "' as type '" + String.class.getName() + "' for field '" + field_.toString() + "'");
                    cell = null;
                }
            } else if (Boolean.class.isAssignableFrom(fieldType) || Boolean.TYPE.isAssignableFrom(fieldType)) {
                String booleanValue = cell.toString().toLowerCase();
                try {
                    cell = Boolean.valueOf(booleanValue);
                } catch (Throwable t) {
                    log.debug("Unable to bind value of '" + cell + "' as type '" + Boolean.class.getName() + "' for field '" + field_.toString() + "'");
                    cell = false;
                }
            } else if (Character.class.isAssignableFrom(fieldType) || Character.TYPE.isAssignableFrom(fieldType)) {
                String characterValue = cell.toString();
                try {
                    cell = characterValue.charAt(0);
                } catch (Throwable t) {
                    log.debug("Unable to bind value of '" + cell + "' as type '" + Character.class.getName() + "' for field '" + field_.toString() + "'");
                    cell = '\u0000';
                }
            } else if (Short.class.isAssignableFrom(fieldType) || Short.TYPE.isAssignableFrom(fieldType)) {
                String booleanValue = cell.toString();
                try {
                    cell = Short.valueOf(booleanValue);
                } catch (Throwable t) {
                    log.debug("Unable to bind value of '" + cell + "' as type '" + Short.class.getName() + "' for field '" + field_.toString() + "'");
                    cell = 0;
                }
            } else if (Integer.class.isAssignableFrom(fieldType) || Integer.TYPE.isAssignableFrom(fieldType)) {
                String booleanValue = cell.toString();
                try {
                    cell = Integer.valueOf(booleanValue);
                } catch (Throwable t) {
                    log.debug("Unable to bind value of '" + cell + "' as type '" + Integer.class.getName() + "' for field '" + field_.toString() + "'");
                    cell = 0;
                }
            } else if (Long.class.isAssignableFrom(fieldType) || Long.TYPE.isAssignableFrom(fieldType)) {
                String booleanValue = cell.toString();
                try {
                    cell = Long.valueOf(booleanValue);
                } catch (Throwable t) {
                    log.debug("Unable to bind value of '" + cell + "' as type '" + Long.class.getName() + "' for field '" + field_.toString() + "'");
                    cell = 0L;
                }
            } else if (Float.class.isAssignableFrom(fieldType) || Float.TYPE.isAssignableFrom(fieldType)) {
                String booleanValue = cell.toString();
                try {
                    cell = Float.valueOf(booleanValue);
                } catch (Throwable t) {
                    log.debug("Unable to bind value of '" + cell + "' as type '" + Float.class.getName() + "' for field '" + field_.toString() + "'");
                    cell = 0.0f;
                }
            } else if (Double.class.isAssignableFrom(fieldType) || Double.TYPE.isAssignableFrom(fieldType)) {
                String booleanValue = cell.toString();
                try {
                    cell = Double.valueOf(booleanValue);
                } catch (Throwable t) {
                    log.debug("Unable to bind value of '" + cell + "' as type '" + Double.class.getName() + "' for field '" + field_.toString() + "'");
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
            String booleanValue = value.toString().toLowerCase();
            try {
                return Boolean.valueOf(booleanValue);
            } catch (Throwable t) {
                log.debug("Unable to bind value of '" + value + "' as type '" + Boolean.class.getName() + "'");
                return false;
            }
        } else if (Character.class.isAssignableFrom(targetType) || Character.TYPE.isAssignableFrom(targetType)) {
            String characterValue = value.toString();
            try {
                return characterValue.charAt(0);
            } catch (Throwable t) {
                log.debug("Unable to bind value of '" + value + "' as type '" + Character.class.getName() + "'");
                return '\u0000';
            }
        } else if (Short.class.isAssignableFrom(targetType) || Short.TYPE.isAssignableFrom(targetType)) {
            String booleanValue = value.toString();
            try {
                return Short.valueOf(booleanValue);
            } catch (Throwable t) {
                log.debug("Unable to bind value of '" + value + "' as type '" + Short.class.getName() + "'");
                return 0;
            }
        } else if (Integer.class.isAssignableFrom(targetType) || Integer.TYPE.isAssignableFrom(targetType)) {
            String booleanValue = value.toString();
            try {
                return Integer.valueOf(booleanValue);
            } catch (Throwable t) {
                log.debug("Unable to bind value of '" + value + "' as type '" + Integer.class.getName() + "'");
                return 0;
            }
        } else if (Long.class.isAssignableFrom(targetType) || Long.TYPE.isAssignableFrom(targetType)) {
            String booleanValue = value.toString();
            try {
                return Long.valueOf(booleanValue);
            } catch (Throwable t) {
                log.debug("Unable to bind value of '" + value + "' as type '" + Long.class.getName() + "'");
                return 0L;
            }
        } else if (Float.class.isAssignableFrom(targetType) || Float.TYPE.isAssignableFrom(targetType)) {
            String booleanValue = value.toString();
            try {
                return Float.valueOf(booleanValue);
            } catch (Throwable t) {
                log.debug("Unable to bind value of '" + value + "' as type '" + Float.class.getName() + "'");
                return 0.0f;
            }
        } else if (Double.class.isAssignableFrom(targetType) || Double.TYPE.isAssignableFrom(targetType)) {
            String booleanValue = value.toString();
            try {
                return Double.valueOf(booleanValue);
            } catch (Throwable t) {
                log.debug("Unable to bind value of '" + value + "' as type '" + Double.class.getName() + "'");
                return 0.0d;
            }
        }
        return null;
    }

}