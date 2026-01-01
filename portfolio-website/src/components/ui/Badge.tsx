import React from 'react';

interface BadgeProps {
    children: React.ReactNode;
    variant?: 'primary' | 'secondary' | 'success' | 'gray';
    size?: 'sm' | 'md';
    className?: string;
}

export const Badge: React.FC<BadgeProps> = ({
    children,
    variant = 'primary',
    size = 'md',
    className = '',
}) => {
    const variants = {
        primary: 'bg-primary-100 text-primary-800',
        secondary: 'bg-secondary-100 text-secondary-800',
        success: 'bg-emerald-100 text-emerald-800',
        gray: 'bg-gray-100 text-gray-800',
    };

    const sizes = {
        sm: 'px-2 py-0.5 text-xs',
        md: 'px-3 py-1 text-sm',
    };

    const classes = `inline-block rounded-full font-medium ${variants[variant]} ${sizes[size]} ${className}`;

    return <span className={classes}>{children}</span>;
};
