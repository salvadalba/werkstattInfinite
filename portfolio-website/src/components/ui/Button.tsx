import React from 'react';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
    variant?: 'primary' | 'secondary' | 'outline' | 'ghost';
    size?: 'sm' | 'md' | 'lg';
    children: React.ReactNode;
    href?: string;
    icon?: React.ReactNode;
    loading?: boolean;
}

export const Button: React.FC<ButtonProps> = ({
    variant = 'primary',
    size = 'md',
    children,
    href,
    icon,
    loading = false,
    className = '',
    disabled,
    ...props
}) => {
    const baseStyles = 'inline-flex items-center justify-center font-semibold rounded-lg transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed';

    const variants = {
        primary: 'bg-primary-600 text-white hover:bg-primary-700 focus:ring-primary-600',
        secondary: 'bg-secondary-600 text-white hover:bg-secondary-700 focus:ring-secondary-600',
        outline: 'border-2 border-primary-600 text-primary-600 hover:bg-primary-50 focus:ring-primary-600',
        ghost: 'text-gray-700 hover:bg-gray-100 focus:ring-gray-600',
    };

    const sizes = {
        sm: 'px-3 py-1.5 text-sm',
        md: 'px-4 py-2 text-base',
        lg: 'px-6 py-3 text-lg',
    };

    const classes = `${baseStyles} ${variants[variant]} ${sizes[size]} ${className}`;

    if (href) {
        return (
            <a
                href={href}
                className={classes}
            >
                {icon && <span className="mr-2">{icon}</span>}
                {loading ? 'Loading...' : children}
            </a>
        );
    }

    return (
        <button
            className={classes}
            disabled={disabled || loading}
            {...props}
        >
            {icon && <span className="mr-2">{icon}</span>}
            {loading ? 'Loading...' : children}
        </button>
    );
};
